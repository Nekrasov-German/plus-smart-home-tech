package ru.yandex.practicum.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "kafka")
public class KafkaMultiConsumerProperties {
    private ConsumerConfig consumer = new ConsumerConfig();
    private HubConsumerConfig hubConsumer = new HubConsumerConfig();
    private SnapshotConsumerConfig snapshotConsumer = new SnapshotConsumerConfig();

    @Getter @Setter
    public static class ConsumerConfig {
        private String bootstrapServers;
        private String autoOffsetReset;
        private int maxPollRecords;
        private int sessionTimeoutMs;
    }

    @Getter @Setter
    public static class HubConsumerConfig {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
    }

    @Getter @Setter
    public static class SnapshotConsumerConfig {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
    }
}
