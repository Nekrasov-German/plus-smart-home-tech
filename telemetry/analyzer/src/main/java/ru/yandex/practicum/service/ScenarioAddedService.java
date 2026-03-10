package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.ScenarioRepository;
import ru.yandex.practicum.dal.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.service.entity.*;

import java.util.HashMap;

@Slf4j
@Service
@Transactional
public class ScenarioAddedService {
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    public ScenarioAddedService(ScenarioRepository scenarioRepository, SensorRepository sensorRepository) {
        this.scenarioRepository = scenarioRepository;
        this.sensorRepository = sensorRepository;
    }

    public void handle(String hubId, ScenarioAddedEventAvro avroEvent) {
        // Конвертируем Avro → Entity
        Scenario scenario = convertToEntity(hubId, avroEvent);

        // Сохраняем в БД
        scenarioRepository.save(scenario);
        log.info("Сценарий сохранён: {} для хаба {}", avroEvent.getName(), hubId);
    }

    private Scenario convertToEntity(String hubId, ScenarioAddedEventAvro avroEvent) {
        Scenario scenario = Scenario.builder()
                .hubId(hubId)
                .name(avroEvent.getName())
                .conditions(new HashMap<>())
                .actions(new HashMap<>())
                .build();

        // Обрабатываем условия
        for (ScenarioConditionAvro condAvro : avroEvent.getConditions()) {
            Condition condition = Condition.builder()
                    .type(condAvro.getType().toString())
                    .operation(condAvro.getOperation().toString())
                    .value(extractValue(condAvro.getValue()))
                    .build();

            // Находим датчик по ID
            Sensor sensor = sensorRepository.findById(condAvro.getSensorId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Датчик не найден: " + condAvro.getSensorId()));

            // Добавляем в Map: ключ — sensorId, значение — Condition
            scenario.getConditions().put(sensor.getId(), condition);
        }

        // Обрабатываем действия
        for (DeviceActionAvro actionAvro : avroEvent.getActions()) {
            Action action = Action.builder()
                    .type(actionAvro.getType().toString())
                    .value(actionAvro.getValue())
                    .build();

            Sensor sensor = sensorRepository.findById(actionAvro.getSensorId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Датчик не найден: " + actionAvro.getSensorId()));

            // Добавляем в Map: ключ — sensorId, значение — Action
            scenario.getActions().put(sensor.getId(), action);
        }

        return scenario;
    }

    private Integer extractValue(Object avroValue) {
        if (avroValue == null) return null;
        if (avroValue instanceof Integer) return (Integer) avroValue;
        if (avroValue instanceof Boolean) return (Boolean) avroValue ? 1 : 0;
        throw new IllegalArgumentException("Неподдерживаемый тип значения: " + avroValue.getClass());
    }
}
