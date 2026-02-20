package ru.practicum.service;

import ru.practicum.model.DeviceEvent;
import ru.practicum.model.SensorEvent;

public interface CollectorService {
    void getSensors(SensorEvent sensorEvent);
    void getHubs(DeviceEvent deviceEvent);
}
