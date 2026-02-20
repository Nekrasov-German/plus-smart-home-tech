package ru.practicum.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.enums.DeviceEventType;

@Getter
@Setter
@ToString
public class DeviceRemovedEvent extends DeviceEvent {
    @Override
    public DeviceEventType getType() {
        return DeviceEventType.DEVICE_REMOVED;
    }
}
