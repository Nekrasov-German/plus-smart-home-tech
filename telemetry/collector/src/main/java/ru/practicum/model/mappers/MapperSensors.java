package ru.practicum.model.mappers;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.model.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@UtilityClass
public class MapperSensors {
    public SpecificRecordBase climateSensorToClimateSensorAvro (SensorEvent event) {
        ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) event;
        return SensorEventAvro.newBuilder()
                .setId(climateSensorEvent.getId())
                .setHubId(climateSensorEvent.getHubId())
                .setTimestamp(climateSensorEvent.getTimestamp())
                .setPayload(ClimateSensorAvro.newBuilder()
                        .setCo2Level(climateSensorEvent.getCo2Level())
                        .setHumidity(climateSensorEvent.getHumidity())
                        .setTemperatureC(climateSensorEvent.getTemperatureC())
                        .build())
                .build();
    }

    public SpecificRecordBase lightSensorToLightSensorAvro (SensorEvent sensorEvent) {
        LightSensorEvent lightSensorEvent = (LightSensorEvent) sensorEvent;
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(LightSensorAvro.newBuilder()
                        .setLinkQuality(lightSensorEvent.getLinkQuality())
                        .setLuminosity(lightSensorEvent.getLuminosity())
                        .build())
                .build();
    }

    public SpecificRecordBase motionSensorToMotionSensorAvro (SensorEvent sensorEvent) {
        MotionSensorEvent motionSensorEvent = (MotionSensorEvent) sensorEvent;
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(MotionSensorAvro.newBuilder()
                        .setLinkQuality(motionSensorEvent.getLinkQuality())
                        .setMotion(motionSensorEvent.isMotion())
                        .setVoltage(motionSensorEvent.getVoltage())
                        .build())
                .build();
    }

    public SpecificRecordBase switchSensorToSwitchSensorAvro (SensorEvent sensorEvent) {
        SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) sensorEvent;
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(SwitchSensorAvro.newBuilder()
                        .setState(switchSensorEvent.isState())
                        .build())
                .build();
    }

    public SpecificRecordBase temperatureSensorToTemperatureSensorAvro (SensorEvent sensorEvent) {
        TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) sensorEvent;
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(TemperatureSensorAvro.newBuilder()
                        .setId(temperatureSensorEvent.getId())
                        .setHubId(temperatureSensorEvent.getHubId())
                        .setTimestamp(temperatureSensorEvent.getTimestamp())
                        .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                        .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                        .build())
                .build();
    }
}
