package ru.yandex.practicum.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.List;

@Slf4j
public abstract class BaseKafkaConsumer {
    protected final KafkaConsumer<String, SpecificRecordBase> kafkaConsumer;

    public BaseKafkaConsumer(KafkaConsumer<String, SpecificRecordBase> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

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
