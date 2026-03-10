package ru.yandex.practicum.configuration;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConsumerConfig {

    private final KafkaMultiConsumerProperties properties;

    public KafkaConsumerConfig(KafkaMultiConsumerProperties properties) {
        this.properties = properties;
    }

    @Bean("hubKafkaConsumer")
    public KafkaConsumer<String, SpecificRecordBase> hubKafkaConsumer() {
        return createKafkaConsumer(
                properties.getHubConsumer().getGroupId(),
                properties.getHubConsumer().getKeyDeserializer(),
                properties.getHubConsumer().getValueDeserializer()
        );
    }

    @Bean("snapshotKafkaConsumer")
    public KafkaConsumer<String, SpecificRecordBase> snapshotKafkaConsumer() {
        return createKafkaConsumer(
                properties.getSnapshotConsumer().getGroupId(),
                properties.getSnapshotConsumer().getKeyDeserializer(),
                properties.getSnapshotConsumer().getValueDeserializer()
        );
    }

    private KafkaConsumer<String, SpecificRecordBase> createKafkaConsumer(
            String groupId, String keyDeserializer, String valueDeserializer) {

        Properties props = new Properties();
        props.put("bootstrap.servers", properties.getConsumer().getBootstrapServers());
        props.put("group.id", groupId);
        props.put("auto.offset.reset", properties.getConsumer().getAutoOffsetReset());
        props.put("enable.auto.commit", false);
        props.put("max.poll.records", properties.getConsumer().getMaxPollRecords());
        props.put("session.timeout.ms", properties.getConsumer().getSessionTimeoutMs());
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);

        return new KafkaConsumer<>(props);
    }
}
