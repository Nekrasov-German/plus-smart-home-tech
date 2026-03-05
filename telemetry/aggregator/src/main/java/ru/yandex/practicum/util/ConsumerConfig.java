package ru.yandex.practicum.util;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.group-id}")
    private String groupId;

    @Value("${kafka.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${kafka.key-deserializer}")
    private String keyDeserializer;

    @Value("${kafka.value-deserializer}")
    private String valueDeserializer;

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> kafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("auto.offset.reset", autoOffsetReset);
        props.put("enable.auto.commit", "false");
        props.put("max.poll.records", 100);
        props.put("session.timeout.ms", 30000);
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);

        return new KafkaConsumer<>(props);
    }
}
