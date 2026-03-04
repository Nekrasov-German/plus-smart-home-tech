package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.util.mappers.MapperSensors;
import ru.yandex.practicum.util.CollectorProducer;

@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {
    private final static String TOPIC_SENSOR = "telemetry.sensors.v1";

    private final CollectorProducer producer;

    @Override
    public void handle(SensorEventProto event) {
        producer.sendMessage(TOPIC_SENSOR, MapperSensors.climateSensorToClimateSensorAvro(event));
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }
}
