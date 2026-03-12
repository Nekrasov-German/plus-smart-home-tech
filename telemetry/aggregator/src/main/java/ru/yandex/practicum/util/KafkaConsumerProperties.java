package ru.yandex.practicum.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {
    private String groupId = "default-group";
    private String autoOffsetReset = "latest";
    private boolean enableAutoCommit = false;
    private int maxPollRecords = 100;
    private int sessionTimeoutMs = 30000;

    // Геттеры и сеттеры
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getAutoOffsetReset() { return autoOffsetReset; }
    public void setAutoOffsetReset(String autoOffsetReset) { this.autoOffsetReset = autoOffsetReset; }

    public boolean isEnableAutoCommit() { return enableAutoCommit; }
    public void setEnableAutoCommit(boolean enableAutoCommit) { this.enableAutoCommit = enableAutoCommit; }

    public int getMaxPollRecords() { return maxPollRecords; }
    public void setMaxPollRecords(int maxPollRecords) { this.maxPollRecords = maxPollRecords; }

    public int getSessionTimeoutMs() { return sessionTimeoutMs; }
    public void setSessionTimeoutMs(int sessionTimeoutMs) { this.sessionTimeoutMs = sessionTimeoutMs; }
}
