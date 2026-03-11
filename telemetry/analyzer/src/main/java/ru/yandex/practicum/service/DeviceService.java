package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.service.entity.Sensor;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeviceService {
    private final SensorRepository sensorRepository;

    public void handleAdd(String hubId, DeviceAddedEventAvro event) {
        String sensorId = event.getId();
        DeviceTypeAvro deviceType = event.getType();

        log.info("Обработка добавления устройства: id={}, тип={}, хаб={}", sensorId, deviceType, hubId);

        if (sensorRepository.existsById(sensorId)) {
            log.warn("Датчик с ID {} уже существует, пропускаем создание", sensorId);
            return;
        }

        Sensor sensor = Sensor.builder()
                .id(sensorId)
                .hubId(hubId)
                .build();

        sensorRepository.save(sensor);
        log.info("Устройство успешно добавлено: id={}, тип={}", sensorId, deviceType);
    }

    public void handleRemove(String hubId, DeviceRemovedEventAvro event) {
        String sensorId = event.getId();

        log.info("Обработка удаления устройства: id={}, хаб={}", sensorId, hubId);

        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Датчик не найден для удаления: " + sensorId));

        if (!sensor.getHubId().equals(hubId)) {
            throw new IllegalArgumentException(
                    String.format("Несоответствие hub_id: в событии %s, в датчике %s", hubId, sensor.getHubId()));
        }

        sensorRepository.delete(sensor);
        log.info("Устройство успешно удалено: id={}", sensorId);
    }
}
