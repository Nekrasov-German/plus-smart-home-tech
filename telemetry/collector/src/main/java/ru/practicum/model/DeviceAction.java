package ru.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.enums.DeviceActionType;

@Getter
@Setter
@ToString
public class DeviceAction {

    @NotNull
    private String sensorId;

    @NotNull
    private DeviceActionType type;

    private int value;
}
