package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.util.mappers.MapperSensors;
import ru.yandex.practicum.util.CollectorProducer;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {
    private final static String TOPIC_SENSOR = "telemetry.sensors.v1";

    private final CollectorProducer producer;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        producer.sendMessage(TOPIC_SENSOR, MapperSensors.lightSensorToLightSensorAvro(event));
    }
}
