package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.enums.DeviceEventType;

@Getter
@Setter
@ToString
public class ScenarioRemovedEvent extends DeviceEvent {

    @NotNull
    private String name;

    @Override
    public DeviceEventType getType() {
        return DeviceEventType.SCENARIO_REMOVED;
    }
}
