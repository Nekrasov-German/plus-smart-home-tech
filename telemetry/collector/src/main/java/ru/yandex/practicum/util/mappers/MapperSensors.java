package ru.yandex.practicum.util.mappers;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@UtilityClass
public class MapperSensors {
    public SpecificRecordBase climateSensorToClimateSensorAvro (SensorEventProto event) {
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(ClimateSensorAvro.newBuilder()
                        .setCo2Level(event.getClimateSensor().getCo2Level())
                        .setHumidity(event.getClimateSensor().getHumidity())
                        .setTemperatureC(event.getClimateSensor().getTemperatureC())
                        .build())
                .build();
    }

    public SpecificRecordBase lightSensorToLightSensorAvro (SensorEventProto sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(),
                        sensorEvent.getTimestamp().getNanos()))
                .setPayload(LightSensorAvro.newBuilder()
                        .setLinkQuality(sensorEvent.getLightSensor().getLinkQuality())
                        .setLuminosity(sensorEvent.getLightSensor().getLuminosity())
                        .build())
                .build();
    }

    public SpecificRecordBase motionSensorToMotionSensorAvro (SensorEventProto sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(),
                        sensorEvent.getTimestamp().getNanos()))
                .setPayload(MotionSensorAvro.newBuilder()
                        .setLinkQuality(sensorEvent.getMotionSensor().getLinkQuality())
                        .setMotion(sensorEvent.getMotionSensor().getMotion())
                        .setVoltage(sensorEvent.getMotionSensor().getVoltage())
                        .build())
                .build();
    }

    public SpecificRecordBase switchSensorToSwitchSensorAvro (SensorEventProto sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(),
                        sensorEvent.getTimestamp().getNanos()))
                .setPayload(SwitchSensorAvro.newBuilder()
                        .setState(sensorEvent.getSwitchSensor().getState())
                        .build())
                .build();
    }

    public SpecificRecordBase temperatureSensorToTemperatureSensorAvro (SensorEventProto sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(),
                        sensorEvent.getTimestamp().getNanos()))
                .setPayload(TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(sensorEvent.getTemperatureSensor().getTemperatureC())
                        .setTemperatureF(sensorEvent.getTemperatureSensor().getTemperatureF())
                        .build())
                .build();
    }
}
