package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.util.HubAnalyzerConsumer;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final static List<String> TOPICS_HUB = List.of("telemetry.hubs.v1");
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(100);

    private final HubAnalyzerConsumer analyzerConsumer;
    private final DeviceService deviceService;
    private final ScenarioService scenarioService;

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
            log.error("Критическая ошибка во время обработки событий от Hub", e);
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

                processHubEvent(record);
                analyzerConsumer.commitSync();
            }
        } catch (WakeupException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обработке партии сообщений", e);
        }
    }

    private void processHubEvent(ConsumerRecord<String, SpecificRecordBase> record) {
        if (!(record.value() instanceof HubEventAvro)) {
            log.warn("Неподдерживаемый тип сообщения: {}", record.value().getClass());
            return;
        }

        HubEventAvro event = (HubEventAvro) record.value();

        try {
            Object payload = event.getPayload();

            switch (payload) {
                case DeviceAddedEventAvro deviceAddedEventAvro -> {
                    deviceService
                            .handleAdd(event.getHubId(), deviceAddedEventAvro);
                }
                case DeviceRemovedEventAvro deviceRemovedEventAvro -> {
                    deviceService
                            .handleRemove(event.getHubId(), deviceRemovedEventAvro);
                }
                case ScenarioAddedEventAvro scenarioAddedEventAvro -> {
                    scenarioService
                            .handleAdd(event.getHubId(), scenarioAddedEventAvro);
                }
                case ScenarioRemovedEventAvro scenarioRemovedEventAvro -> {
                    scenarioService
                            .handleRemove(event.getHubId(), scenarioRemovedEventAvro);
                }
                case null, default -> log.warn("Неизвестный тип событие {}",
                        payload != null ? payload.getClass().getName() : null);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события для хаба {}: {}",
                    event.getHubId(), e.getMessage(), e);
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
