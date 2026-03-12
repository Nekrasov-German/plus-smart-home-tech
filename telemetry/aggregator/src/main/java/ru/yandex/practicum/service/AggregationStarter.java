package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.util.AggregatorConsumer;
import ru.yandex.practicum.util.AggregatorProducer;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final ConcurrentMap<String, SensorsSnapshotAvro> snapShots = new ConcurrentHashMap<>();

    private final static List<String> TOPICS = List.of("telemetry.sensors.v1");
    private final static String TOPIC_SNAPSHOT = "telemetry.snapshots.v1";
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(100);

    private final AggregatorProducer producer;
    private final AggregatorConsumer consumer;
    private volatile boolean running = true;

    public void start() {
        consumer.subscribe(TOPICS);

        try {
            while (running) {
                processBatch();
            }
        } catch (WakeupException e) {
            log.info("Агрегация остановлена по сигналу wakeup");
        } catch (Exception e) {
            log.error("Критическая ошибка во время обработки событий от датчиков", e);
        } finally {
            cleanup();
        }
    }

    private void processBatch() {
        try {
            ConsumerRecords<String, SpecificRecordBase> records = consumer.pollMessages(POLL_TIMEOUT);

            for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                log.info("Получено сообщение из топика '{}', partition: {}, offset: {}, key: {}",
                        record.topic(), record.partition(), record.offset(), record.key());

                processSensorEvent(record);
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обработке партии сообщений", e);
        }
    }

    private void processSensorEvent(ConsumerRecord<String, SpecificRecordBase> record) {
        if (!(record.value() instanceof SensorEventAvro)) {
            log.warn("Неподдерживаемый тип сообщения: {}", record.value().getClass());
            return;
        }

        SensorEventAvro event = (SensorEventAvro) record.value();

        Optional<SensorsSnapshotAvro> updatedSnapshot = updateState(event);
        updatedSnapshot.ifPresent(snapshot -> {
            producer.sendMessage(TOPIC_SNAPSHOT, event.getHubId(), snapshot);
            log.info("Отправлен обновлённый снимок для хаба: {}", event.getHubId());
        });
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();

        SensorsSnapshotAvro snapshot = snapShots.computeIfAbsent(hubId, h -> createNewSnapshot(h, event.getTimestamp()));

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        SensorStateAvro oldState = sensorsState.get(sensorId);

        if (shouldUpdateSensorState(oldState, event)) {
            SensorStateAvro newState = createSensorState(event);
            sensorsState.put(sensorId, newState);

            snapshot.setTimestamp(event.getTimestamp());

            snapShots.put(hubId, snapshot);
            return Optional.of(snapshot);
        }

        return Optional.empty();
    }

    private boolean shouldUpdateSensorState(SensorStateAvro oldState, SensorEventAvro event) {
        if (oldState == null) {
            return true;
        }

        if (event.getTimestamp().isAfter(oldState.getTimestamp())) {
            return true;
        }

        if (event.getTimestamp().equals(oldState.getTimestamp())) {
            Object oldPayload = oldState.getData();
            Object newPayload = event.getPayload();
            return !arePayloadsEqual(oldPayload, newPayload);
        }

        return false;
    }

    private boolean arePayloadsEqual(Object oldPayload, Object newPayload) {
        if (oldPayload == null && newPayload == null) return true;
        if (oldPayload == null || newPayload == null) return false;

        return oldPayload.equals(newPayload);
    }

    private SensorStateAvro createSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }

    private SensorsSnapshotAvro createNewSnapshot(String hubId, Instant timestamp) {
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(timestamp)
                .setSensorsState(new HashMap<>())
                .build();
    }

    private void cleanup() {
        try {
            producer.flush();
            log.info("Буфер продюсера сброшен");
        } catch (Exception e) {
            log.warn("Ошибка при сбросе буфера продюсера", e);
        }

        try {
            consumer.stop();
            log.info("Консьюмер остановлен");
        } catch (Exception e) {
            log.warn("Ошибка при остановке консьюмера", e);
        }

        try {
            producer.close();
            log.info("Продюсер закрыт");
        } catch (Exception e) {
            log.warn("Ошибка при закрытии продюсера", e);
        }
    }

    public void stopAggregation() {
        running = false;
        consumer.stop(); // Прерываем poll() если он выполняется
    }
}
