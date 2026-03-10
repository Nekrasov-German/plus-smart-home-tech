package ru.yandex.practicum;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubDeserializer extends BaseAvroDeserializer<HubEventAvro> {
    public HubDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}
