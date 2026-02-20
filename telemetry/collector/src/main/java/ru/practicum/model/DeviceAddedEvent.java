package ru.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.enums.DeviceEventType;
import ru.practicum.model.enums.DeviceType;

@Getter
@Setter
@ToString
public class DeviceAddedEvent extends DeviceEvent {

    @NotNull
    private DeviceType deviceType;

    @Override
    public DeviceEventType getType() {
        return DeviceEventType.DEVICE_ADDED;
    }
}
