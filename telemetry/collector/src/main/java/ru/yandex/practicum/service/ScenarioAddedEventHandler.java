package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.util.mappers.MapperHubs;
import ru.yandex.practicum.util.CollectorProducer;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final static String TOPIC_HUB = "telemetry.hubs.v1";

    private final CollectorProducer producer;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        producer.sendMessage(TOPIC_HUB, MapperHubs.scenarioAddedEventToScenarioAddedEventAvro(event));
    }
}
