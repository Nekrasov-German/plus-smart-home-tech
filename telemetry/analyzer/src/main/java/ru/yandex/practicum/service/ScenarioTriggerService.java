package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.entity.Condition;
import ru.yandex.practicum.service.entity.Scenario;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioTriggerService {

    private final ScenarioRepository scenarioRepository;
    private final HubRouterGrpcClient hubRouterGrpcClient;

    /**
     * Обрабатывает снапшот датчиков: проверяет сценарии и выполняет действия
     */
    public void processSnapshotAndTriggerScenarios(String hubId, SensorsSnapshotAvro event) {
        // Получаем все активные сценарии для хаба
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        for (Scenario scenario : scenarios) {
            if (shouldTriggerScenario(scenario, event)) {
                executeScenarioActions(scenario, hubId);
            }
        }
    }

    /**
     * Проверяет, выполняются ли условия сценария для текущего состояния датчиков
     */
    private boolean shouldTriggerScenario(Scenario scenario, SensorsSnapshotAvro event) {
        Map<String, Condition> conditions = scenario.getConditions();

        for (Map.Entry<String, Condition> entry : conditions.entrySet()) {
            String sensorId = entry.getKey();
            Condition condition = entry.getValue();

            SensorStateAvro sensorState = event.getSensorsState().get(sensorId);
            if (sensorState == null) {
                return false; // Датчик не в снапшоте
            }
            if (!evaluateCondition(condition, sensorState)) {
                return false; // Условие не выполнено
            }
        }
        return true; // Все условия выполнены
    }

    /**
     * Оценивает одно условие против состояния датчика
     */
    private boolean evaluateCondition(Condition cond, SensorStateAvro sensorState) {
        Object payload = sensorState.getData();

        try {
            switch (payload) {
                case MotionSensorAvro motion -> {
                    int motionValue = motion.getMotion() ? 1 : 0;
                    if (evaluateEqualsCondition(cond, motionValue)) {
                        return true;
                    }
                }
                case LightSensorAvro light -> {
                    if (evaluateEqualsCondition(cond, light.getLinkQuality())
                            || evaluateEqualsCondition(cond, light.getLuminosity())) {
                        return true;
                    }
                }
                case SwitchSensorAvro sw -> {
                    int swValue = sw.getState() ? 1 : 0;
                    if (evaluateEqualsCondition(cond, swValue)) {
                        return true;
                    }
                }
                case TemperatureSensorAvro temp -> {
                    if (evaluateEqualsCondition(cond, temp.getTemperatureC())
                            || evaluateEqualsCondition(cond, temp.getTemperatureF())) {
                        return true;
                    }
                }
                case ClimateSensorAvro climate -> {
                    if (evaluateEqualsCondition(cond, climate.getCo2Level())
                            || evaluateEqualsCondition(cond, climate.getHumidity())
                            || evaluateEqualsCondition(cond, climate.getTemperatureC())) {
                        return true;
                    }
                }
                case null, default -> log.warn("Неподдерживаемый тип датчика: {} (класс: {}) для датчика",
                        payload.getClass().getSimpleName(), payload.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Неизвестный тип условия: {}", cond.getType(), e);
        }
        return false;
    }

    private boolean evaluateEqualsCondition(Condition cond, Integer temp) {
        int value = cond.getValue();
        switch (ConditionOperationAvro.valueOf(cond.getOperation())) {
            case GREATER_THAN -> {
                return temp > value;
            }
            case LOWER_THAN -> {
                return temp < value;
            }
            case EQUALS -> {
                return temp == value;
            }
            default -> throw new IllegalArgumentException("Неизвестная операция: " + cond.getOperation());
        }
    }

    /**
     * Выполняет действия сценария через gRPC‑вызов
     */
    private void executeScenarioActions(Scenario scenario, String hubId) {
        hubRouterGrpcClient.handleDeviceAction(scenario, hubId);
    }
}
