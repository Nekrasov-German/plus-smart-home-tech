package ru.yandex.practicum.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectorProducer {
    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    public <T extends SpecificRecordBase> void sendMessage(String topic, String key, T avroMessage) {
        ProducerRecord<String, T> record = new ProducerRecord<>(topic, key, avroMessage);

        kafkaProducer.send((ProducerRecord<String, SpecificRecordBase>) record, (metadata, exception) -> {
            if (exception == null) {
                log.info("Сообщение отправлено в топик '{}', partition: {}, offset: {}",
                        topic, metadata.partition(), metadata.offset());
            } else {
                log.error("Ошибка отправки сообщения в топик '{}'", topic, exception);
            }
        });
    }

    public <T extends SpecificRecordBase> void sendMessage(String topic, T avroMessage) {
        sendMessage(topic, null, avroMessage);
    }

    public void flush() {
        kafkaProducer.flush();
    }

    public void close() {
        log.info("Закрытие Kafka Producer...");
        kafkaProducer.close();
    }
}
