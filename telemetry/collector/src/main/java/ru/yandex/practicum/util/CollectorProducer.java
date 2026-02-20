package ru.yandex.practicum.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class CollectorProducer {
    @Autowired
    private KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public void sendMessage(String topic, SpecificRecordBase message) {
        Object futureObj = kafkaTemplate.send(topic, message);

        if (futureObj instanceof CompletableFuture) {
            CompletableFuture<SendResult<String, SpecificRecordBase>> future =
                    (CompletableFuture<SendResult<String, SpecificRecordBase>>) futureObj;
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Ошибка отправки сообщения в топик {}", topic, ex);
                } else {
                    log.info("Сообщение успешно отправлено в топик {}", topic);
                }
            });
        } else if (futureObj instanceof ListenableFuture) {
            ListenableFuture<SendResult<String, SpecificRecordBase>> future =
                    (ListenableFuture<SendResult<String, SpecificRecordBase>>) futureObj;
            future.addCallback(
                    result -> log.info("Сообщение успешно отправлено в топик {}", topic),
                    ex -> log.error("Ошибка отправки сообщения в топик {}", topic, ex)
            );
        }
    }
}
