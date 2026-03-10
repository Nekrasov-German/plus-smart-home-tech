package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.entity.HubEventType;

@Slf4j
@Service
public class HubEventRouter {
    private final DeviceAddedService deviceAddedService;
    private final DeviceRemovedService deviceRemovedService;
    private final ScenarioAddedService scenarioAddedService;
    private final ScenarioRemovedService scenarioRemovedService;

    public HubEventRouter(DeviceAddedService deviceAddedService,
                          DeviceRemovedService deviceRemovedService,
                          ScenarioAddedService scenarioAddedService,
                          ScenarioRemovedService scenarioRemovedService) {
        this.deviceAddedService = deviceAddedService;
        this.deviceRemovedService = deviceRemovedService;
        this.scenarioAddedService = scenarioAddedService;
        this.scenarioRemovedService = scenarioRemovedService;
    }

    public void routeEvent(HubEventAvro event) {
        HubEventType eventType = determineEventType(event);

        log.info("Получено событие для хаба: {}, тип: {}",
                event.getHubId(),
                determineEventType(event));

        switch (eventType) {
            case DEVICE_ADDED -> deviceAddedService
                    .handle(event.getHubId(), (DeviceAddedEventAvro) event.getPayload());
            case DEVICE_REMOVED -> deviceRemovedService
                    .handle(event.getHubId(), (DeviceRemovedEventAvro) event.getPayload());
            case SCENARIO_ADDED -> scenarioAddedService
                    .handle(event.getHubId(), (ScenarioAddedEventAvro) event.getPayload());
            case SCENARIO_REMOVED -> scenarioRemovedService
                    .handle(event.getHubId(), (ScenarioRemovedEventAvro) event.getPayload());
            default -> log.warn("Неизвестный тип события: {}", eventType);
        }
    }

    private HubEventType determineEventType(HubEventAvro event) {
        Object payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro) return HubEventType.DEVICE_ADDED;
        if (payload instanceof DeviceRemovedEventAvro) return HubEventType.DEVICE_REMOVED;
        if (payload instanceof ScenarioAddedEventAvro) return HubEventType.SCENARIO_ADDED;
        if (payload instanceof ScenarioRemovedEventAvro) return HubEventType.SCENARIO_REMOVED;

        throw new IllegalArgumentException("Unsupported payload type: " + payload.getClass());
    }
}
