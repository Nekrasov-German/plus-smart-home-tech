package ru.yandex.practicum;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class CollectorDeserializer extends BaseAvroDeserializer<SensorEventAvro> {
    public CollectorDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}
