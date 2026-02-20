package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.DeviceEvent;
import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.model.enums.DeviceEventType;
import ru.yandex.practicum.model.enums.SensorEventType;
import ru.yandex.practicum.model.mappers.MapperHubs;
import ru.yandex.practicum.model.mappers.MapperSensors;
import ru.yandex.practicum.util.CollectorProducer;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final static String TOPIC_SENSOR = "telemetry.sensors.v1";
    private final static String TOPIC_HUB = "telemetry.hubs.v1";

    private final CollectorProducer producer;

    @Override
    public void getSensors(SensorEvent sensorEvent) {
        log.info(sensorEvent.toString());
        log.info("SENSORS " + sensorEvent.getType().toString());
        switch (sensorEvent.getType()) {
            case SensorEventType.CLIMATE_SENSOR_EVENT -> producer
                    .sendMessage(TOPIC_SENSOR, MapperSensors.climateSensorToClimateSensorAvro(sensorEvent));
            case SensorEventType.LIGHT_SENSOR_EVENT -> producer
                    .sendMessage(TOPIC_SENSOR, MapperSensors.lightSensorToLightSensorAvro(sensorEvent));
            case SensorEventType.MOTION_SENSOR_EVENT -> producer
                    .sendMessage(TOPIC_SENSOR, MapperSensors.motionSensorToMotionSensorAvro(sensorEvent));
            case SensorEventType.SWITCH_SENSOR_EVENT -> producer
                    .sendMessage(TOPIC_SENSOR, MapperSensors.switchSensorToSwitchSensorAvro(sensorEvent));
            case SensorEventType.TEMPERATURE_SENSOR_EVENT -> producer
                    .sendMessage(TOPIC_SENSOR, MapperSensors.temperatureSensorToTemperatureSensorAvro(sensorEvent));
            default -> log.info("Неизвестный датчик" + sensorEvent.toString());
        }

    }

    @Override
    public void getHubs(DeviceEvent deviceEvent) {
        log.info(deviceEvent.toString());
        log.info("DEVICES " + deviceEvent.getType().toString());
        switch (deviceEvent.getType()) {
            case DeviceEventType.DEVICE_ADDED -> producer
                    .sendMessage(TOPIC_HUB, MapperHubs.deviceAddedEventToDeviceAddedEventAvro(deviceEvent));
            case DeviceEventType.DEVICE_REMOVED -> producer
                    .sendMessage(TOPIC_HUB, MapperHubs.deviceRemovedEventToDeviceRemovedEventAvro(deviceEvent));
            case DeviceEventType.SCENARIO_ADDED -> producer
                    .sendMessage(TOPIC_HUB, MapperHubs.scenarioAddedEventToScenarioAddedEventAvro(deviceEvent));
            case DeviceEventType.SCENARIO_REMOVED -> producer
                    .sendMessage(TOPIC_HUB, MapperHubs.scenarioRemovedEventToScenarioRemovedEventAvro(deviceEvent));
            default -> log.info("Неизвестное устройство" + deviceEvent.toString());
        }
    }
}
