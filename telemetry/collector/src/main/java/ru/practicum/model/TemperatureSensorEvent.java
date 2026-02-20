package ru.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.enums.SensorEventType;

@Getter
@Setter
@ToString
public class TemperatureSensorEvent extends SensorEvent {

    @NotNull
    private int temperatureC;

    @NotNull
    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
