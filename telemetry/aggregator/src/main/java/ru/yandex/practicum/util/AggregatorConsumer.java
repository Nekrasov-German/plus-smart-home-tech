package ru.yandex.practicum.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorConsumer {
    private final KafkaConsumer<String, SpecificRecordBase> kafkaConsumer;

    public ConsumerRecords<String, SpecificRecordBase> pollMessages(Duration timeout) {
        return kafkaConsumer.poll(timeout);
    }

    public void subscribe(List<String> topics) {
        kafkaConsumer.subscribe(topics);
    }

    public void stop() {
        log.info("Остановка Kafka Consumer...");
        kafkaConsumer.wakeup();
    }

    public void commitSync() {
        kafkaConsumer.commitSync();
    }
}
