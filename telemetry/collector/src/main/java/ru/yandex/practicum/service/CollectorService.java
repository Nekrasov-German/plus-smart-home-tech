package ru.yandex.practicum.service;

import ru.yandex.practicum.model.DeviceEvent;
import ru.yandex.practicum.model.SensorEvent;

public interface CollectorService {
    void getSensors(SensorEvent sensorEvent);
    void getHubs(DeviceEvent deviceEvent);
}
