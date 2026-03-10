package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.util.SnapshotAnalyzerConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private final static List<String> TOPICS_HUB = List.of("telemetry.snapshots.v1");
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(100);

    private final SnapshotAnalyzerConsumer analyzerConsumer;
    private final ScenarioTriggerService scenarioTriggerService;

    @Override
    public void run() {
        analyzerConsumer.subscribe(TOPICS_HUB);

        try {
            while (true) {
                processBatch();
            }
        } catch (WakeupException e) {
            log.info("Анализ остановлен по сигналу wakeup");
        } catch (Exception e) {
            log.error("Критическая ошибка во время обработки событий от SnapShots", e);
        } finally {
            cleanup();
        }
    }

    private void processBatch() {
        try {
            ConsumerRecords<String, SpecificRecordBase> records = analyzerConsumer.pollMessages(POLL_TIMEOUT);

            for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                log.info("Получено сообщение из топика '{}', partition: {}, offset: {}, key: {}",
                        record.topic(), record.partition(), record.offset(), record.key());

                processSensorSnapshotEvent(record);
                analyzerConsumer.commitSync();
            }
        } catch (WakeupException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обработке партии сообщений", e);
        }
    }

    private void processSensorSnapshotEvent(ConsumerRecord<String, SpecificRecordBase> record) {
        if (!(record.value() instanceof SensorsSnapshotAvro)) {
            log.warn("Неподдерживаемый тип сообщения: {}", record.value().getClass());
            return;
        }

        SensorsSnapshotAvro event = (SensorsSnapshotAvro) record.value();
        String hubId = event.getHubId();
        Map<String, SensorStateAvro> sensorsState = event.getSensorsState();

        log.info("Обрабатывается снапшот для хаба: {}, датчиков: {}", hubId, sensorsState.size());

        try {
            scenarioTriggerService.processSnapshotAndTriggerScenarios(hubId, sensorsState);
        } catch (Exception e) {
            log.error("Ошибка обработки снапшота для хаба {}: {}", hubId, e.getMessage(), e);
        }
    }

    private void cleanup() {
        try {
            analyzerConsumer.stop();
            log.info("Консьюмер остановлен");
        } catch (Exception e) {
            log.warn("Ошибка при остановке консьюмера", e);
        }
    }
}
