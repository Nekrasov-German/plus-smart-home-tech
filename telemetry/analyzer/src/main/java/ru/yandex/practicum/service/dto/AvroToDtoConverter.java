package ru.yandex.practicum.service.dto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Component
public class AvroToDtoConverter {

    public SensorsSnapshotDto convert(SensorsSnapshotAvro avroEvent) {
        SensorsSnapshotDto dto = new SensorsSnapshotDto();
        dto.setHubId(avroEvent.getHubId());
        dto.setTimestamp(convertTimestamp(avroEvent.getTimestamp()));

        Map<String, SensorStateDto> sensorsState = new HashMap<>();
        for (Map.Entry<String, SensorStateAvro> entry : avroEvent.getSensorsState().entrySet()) {
            sensorsState.put(entry.getKey(), convertSensorState(entry.getValue()));
        }
        dto.setSensorsState(sensorsState);

        return dto;
    }

    private SensorStateDto convertSensorState(SensorStateAvro avroState) {
        SensorStateDto dto = new SensorStateDto();
        dto.setTimestamp(convertTimestamp(avroState.getTimestamp()));
        dto.setData(extractSensorData(avroState));
        return dto;
    }

    private SensorDataDto extractSensorData(SensorStateAvro avroState) {
        SensorDataDto data = new SensorDataDto();
        Object payload = avroState.getData();

        if (payload instanceof ClimateSensorAvro) {
            ClimateSensorAvro climate = (ClimateSensorAvro) payload;
            data.setTemperatureC(climate.getTemperatureC());
            data.setHumidity(climate.getHumidity());
            data.setCo2Level(climate.getCo2Level());
        } else if (payload instanceof LightSensorAvro) {
            LightSensorAvro light = (LightSensorAvro) payload;
            data.setLinkQuality(light.getLinkQuality());
            data.setLuminosity(light.getLuminosity());
        } else if (payload instanceof MotionSensorAvro) {
            MotionSensorAvro motion = (MotionSensorAvro) payload;
            data.setMotion(motion.getMotion());
            data.setVoltage(motion.getVoltage());
        } else if (payload instanceof SwitchSensorAvro) {
            SwitchSensorAvro sw = (SwitchSensorAvro) payload;
            data.setState(sw.getState());
        } else if (payload instanceof TemperatureSensorAvro) {
            TemperatureSensorAvro temp = (TemperatureSensorAvro) payload;
            data.setTemperatureC(temp.getTemperatureC());
            data.setTemperatureF(temp.getTemperatureF());
        }

        return data;
    }

    private LocalDateTime convertTimestamp(Instant timestampMs) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestampMs.toEpochMilli()),
                ZoneOffset.UTC);
    }
}
