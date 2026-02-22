package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.enums.DeviceEventType;

import java.util.List;

@Getter
@Setter
@ToString
public class ScenarioAddedEvent extends DeviceEvent {

    @NotNull
    private String name;

    @NotNull
    private List<ScenarioCondition> conditions;

    @NotNull
    private List<DeviceAction> actions;

    @Override
    public DeviceEventType getType() {
        return DeviceEventType.SCENARIO_ADDED;
    }
}
