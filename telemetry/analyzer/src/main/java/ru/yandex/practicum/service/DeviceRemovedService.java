package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.service.entity.Sensor;

@Slf4j
@Service
@Transactional
public class DeviceRemovedService {
    private final SensorRepository sensorRepository;

    public DeviceRemovedService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    /**
     * Обрабатывает событие удаления устройства
     * @param hubId идентификатор хаба из HubEventAvro
     * @param event событие удаления устройства
     */
    public void handle(String hubId, DeviceRemovedEventAvro event) {
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
