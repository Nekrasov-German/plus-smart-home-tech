package ru.yandex.practicum.util;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("snapshotAnalyzerConsumer")
public class SnapshotAnalyzerConsumer extends BaseKafkaConsumer {

    public SnapshotAnalyzerConsumer(@Qualifier("snapshotKafkaConsumer")
                                    KafkaConsumer<String, SpecificRecordBase> kafkaConsumer) {
        super(kafkaConsumer);
    }
}
