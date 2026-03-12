package ru.yandex.practicum.util.mappers;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@UtilityClass
public class MapperHubs {
    public SpecificRecordBase deviceAddedEventToDeviceAddedEventAvro(HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(DeviceAddedEventAvro.newBuilder()
                        .setId(event.getDeviceAdded().getId())
                        .setType(DeviceTypeAvro.valueOf(event.getDeviceAdded().getType().toString()))
                        .build())
                .build();
    }

    public SpecificRecordBase deviceRemovedEventToDeviceRemovedEventAvro(HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(DeviceAddedEventAvro.newBuilder().build())
                .build();
    }

    public SpecificRecordBase scenarioAddedEventToScenarioAddedEventAvro (HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(ScenarioAddedEventAvro.newBuilder()
                        .setName(event.getScenarioAdded().getName())
                        .setActions(event.getScenarioAdded().getActionList().stream()
                                .map(MapperHubs::deviceActionToDeviceActionAvro)
                                .toList())
                        .setConditions(event.getScenarioAdded().getConditionList().stream()
                                .map(MapperHubs::scenarioConditionToScenarioConditionAvro)
                                .toList())
                        .build())
                .build();
    }

    public SpecificRecordBase scenarioRemovedEventToScenarioRemovedEventAvro (HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(ScenarioRemovedEventAvro.newBuilder()
                        .setName(event.getScenarioRemoved().getName())
                        .build())
                .build();
    }

    public DeviceActionAvro deviceActionToDeviceActionAvro(DeviceActionProto deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().toString()))
                .setSensorId(deviceAction.getSensorId())
                .setValue(deviceAction.getValue())
                .build();
    }

    public ScenarioConditionAvro scenarioConditionToScenarioConditionAvro (ScenarioConditionProto scenarioCondition) {
        if (scenarioCondition.getOperation().equals(ConditionOperationProto.EQUALS)) {
            return ScenarioConditionAvro.newBuilder()
                    .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().toString()))
                    .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().toString()))
                    .setSensorId(scenarioCondition.getSensorId())
                    .setValue(scenarioCondition.getBoolValue())
                    .build();
        } else {
            return ScenarioConditionAvro.newBuilder()
                    .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().toString()))
                    .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().toString()))
                    .setSensorId(scenarioCondition.getSensorId())
                    .setValue(scenarioCondition.getIntValue())
                    .build();
        }
    }
}
