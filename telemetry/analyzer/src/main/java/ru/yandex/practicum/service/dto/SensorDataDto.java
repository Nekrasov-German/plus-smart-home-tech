package ru.yandex.practicum.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataDto {
    // ClimateSensorAvro
    private Integer temperatureC;
    private Integer humidity;
    private Integer co2Level;

    // LightSensorAvro
    private Integer linkQuality;
    private Integer luminosity;

    // MotionSensorAvro
    private Boolean motion;
    private Integer voltage;

    // SwitchSensorAvro
    private Boolean state;

    // TemperatureSensorAvro
    private Integer temperatureF;
}
