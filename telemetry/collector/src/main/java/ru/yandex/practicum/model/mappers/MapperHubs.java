package ru.yandex.practicum.model.mappers;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;

@UtilityClass
public class MapperHubs {
    public SpecificRecordBase deviceAddedEventToDeviceAddedEventAvro(DeviceEvent event) {
        DeviceAddedEvent deviceAdded = (DeviceAddedEvent) event;
        return HubEventAvro.newBuilder()
                .setHubId(deviceAdded.getHubId())
                .setTimestamp(deviceAdded.getTimestamp())
                .setPayload(DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAdded.getId())
                        .setType(DeviceTypeAvro.valueOf(deviceAdded.getDeviceType().toString()))
                        .build())
                .build();
    }

    public SpecificRecordBase deviceRemovedEventToDeviceRemovedEventAvro(DeviceEvent event) {
        DeviceRemovedEvent deviceRemoved = (DeviceRemovedEvent) event;
        return HubEventAvro.newBuilder()
                .setHubId(deviceRemoved.getHubId())
                .setTimestamp(deviceRemoved.getTimestamp())
                .setPayload(DeviceAddedEventAvro.newBuilder().build())
                .build();
    }

    public SpecificRecordBase scenarioAddedEventToScenarioAddedEventAvro (DeviceEvent event) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
        return HubEventAvro.newBuilder()
                .setHubId(scenarioAddedEvent.getHubId())
                .setTimestamp(scenarioAddedEvent.getTimestamp())
                .setPayload(ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAddedEvent.getName())
                        .setActions(scenarioAddedEvent.getActions().stream()
                                .map(MapperHubs::deviceActionToDeviceActionAvro)
                                .toList())
                        .setConditions(scenarioAddedEvent.getConditions().stream()
                                .map(MapperHubs::scenarioConditionToScenarioConditionAvro)
                                .toList())
                        .build())
                .build();
    }

    public SpecificRecordBase scenarioRemovedEventToScenarioRemovedEventAvro (DeviceEvent event) {
        ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
        return HubEventAvro.newBuilder()
                .setHubId(scenarioRemovedEvent.getHubId())
                .setTimestamp(scenarioRemovedEvent.getTimestamp())
                .setPayload(ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemovedEvent.getName())
                        .build())
                .build();
    }

    public DeviceActionAvro deviceActionToDeviceActionAvro(DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().toString()))
                .setSensorId(deviceAction.getSensorId())
                .setValue(deviceAction.getValue())
                .build();
    }

    public ScenarioConditionAvro scenarioConditionToScenarioConditionAvro (ScenarioCondition scenarioCondition) {
        return ScenarioConditionAvro.newBuilder()
                .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().toString()))
                .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().toString()))
                .setSensorId(scenarioCondition.getSensorId())
                .setValue(scenarioCondition.getValue())
                .build();
    }
}
