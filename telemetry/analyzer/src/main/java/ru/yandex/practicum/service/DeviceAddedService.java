package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.service.entity.Sensor;

@Slf4j
@Service
@Transactional
public class DeviceAddedService {
    private final SensorRepository sensorRepository;

    public DeviceAddedService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    /**
     * Обрабатывает событие добавления устройства
     * @param hubId идентификатор хаба из HubEventAvro
     * @param event событие добавления устройства
     */
    public void handle(String hubId, DeviceAddedEventAvro event) {
        String sensorId = event.getId();
        DeviceTypeAvro deviceType = event.getType();

        log.info("Обработка добавления устройства: id={}, тип={}, хаб={}", sensorId, deviceType, hubId);

        // Проверяем, не существует ли уже датчик с таким ID
        if (sensorRepository.existsById(sensorId)) {
            log.warn("Датчик с ID {} уже существует, пропускаем создание", sensorId);
            return;
        }

        // Создаём сущность датчика
        Sensor sensor = Sensor.builder()
                .id(sensorId)
                .hubId(hubId)
                .build();//new Sensor(sensorId, hubId);

        // Сохраняем в БД
        sensorRepository.save(sensor);
        log.info("Устройство успешно добавлено: id={}, тип={}", sensorId, deviceType);
    }
}
