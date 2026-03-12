package ru.yandex.practicum.util;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ProducerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.key-serializer}")
    private String keySerializer;

    @Value("${kafka.value-serializer}")
    private String valueSerializer;

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueSerializer);

        return new KafkaProducer<>(props);
    }
}
