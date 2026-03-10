package ru.yandex.practicum.service;

import io.grpc.StatusRuntimeException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.ScenarioRepository;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.dto.SensorDataDto;
import ru.yandex.practicum.service.entity.Action;
import ru.yandex.practicum.service.entity.Condition;
import ru.yandex.practicum.service.entity.Scenario;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class ScenarioTriggerService {

    private final ScenarioRepository scenarioRepository;
    private final HubRouterGrpcClient hubRouterGrpcClient;

    public ScenarioTriggerService(
            ScenarioRepository scenarioRepository,
            HubRouterGrpcClient hubRouterGrpcClient) {
        this.scenarioRepository = scenarioRepository;
        this.hubRouterGrpcClient = hubRouterGrpcClient;
    }

    /**
     * Обрабатывает снапшот датчиков: проверяет сценарии и выполняет действия
     */
    public void processSnapshotAndTriggerScenarios(String hubId, Map<String, SensorStateAvro> sensorsState) {
        // Получаем все активные сценарии для хаба
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        for (Scenario scenario : scenarios) {
            if (shouldTriggerScenario(scenario, sensorsState)) {
                executeScenarioActions(scenario, hubId);
            }
        }
    }

    /**
     * Проверяет, выполняются ли условия сценария для текущего состояния датчиков
     */
    private boolean shouldTriggerScenario(Scenario scenario, Map<String, SensorStateAvro> sensorsState) {
        Map<String, Condition> conditions = scenario.getConditions();

        for (Map.Entry<String, Condition> entry : conditions.entrySet()) {
            String sensorId = entry.getKey();
            Condition condition = entry.getValue();

            SensorStateAvro sensorState = sensorsState.get(sensorId);
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
        SensorDataDto sensorData = extractSensorData(sensorState);

        try {
            ConditionTypeAvro conditionType = ConditionTypeAvro.valueOf(cond.getType());

            switch (conditionType) {
                case MOTION -> {
                    Boolean motion = sensorData.getMotion();
                    if (motion == null) {
                        return false;
                    }
                    return evaluateMotionCondition(cond, motion);
                }
                case TEMPERATURE -> {
                    Integer temp = sensorData.getTemperatureC();
                    if (temp == null) {
                        return false;
                    }
                    return evaluateTemperatureCondition(cond, temp);
                }
                case LUMINOSITY -> {
                    Integer luminosity = sensorData.getLuminosity();
                    if (luminosity == null) {
                        return false;
                    }
                    return evaluateLuminosityCondition(cond, luminosity);
                }
                case CO2LEVEL -> {
                    Integer co2 = sensorData.getCo2Level();
                    if (co2 == null) {
                        return false;
                    }
                    return evaluateCo2Condition(cond, co2);
                }
                case HUMIDITY -> {
                    Integer humidity = sensorData.getHumidity();
                    if (humidity == null) {
                        return false;
                    }
                    return evaluateHumidityCondition(cond, humidity);
                }
                case SWITCH -> {
                    Boolean state = sensorData.getState();
                    if (state == null) {
                        return false;
                    }
                    return evaluateSwitchCondition(cond, state);
                }
                default -> {
                    log.warn("Неподдерживаемый тип условия: {}", conditionType);
                    return false;
                }
            }
        } catch (IllegalArgumentException e) {
            log.error("Неизвестный тип условия: {}", cond.getType(), e);
            return false;
        }
    }

    private boolean evaluateMotionCondition(Condition cond, Boolean motion) {
        switch (ConditionOperationAvro.valueOf(cond.getOperation())) {
            case EQUALS -> {
                return Boolean.valueOf(String.valueOf(cond.getValue())).equals(motion);
            }
            default -> throw new IllegalArgumentException("Операция не поддерживается для типа MOTION");
        }
    }

    private boolean evaluateTemperatureCondition(Condition cond, Integer temp) {
        int value = cond.getValue();
        switch (ConditionOperationAvro.valueOf(cond.getOperation())) {
            case GREATER_THAN -> {
                return temp > value;
            }
            case LOWER_THAN -> {
                return temp < value;
            }
            case EQUALS -> {
                return temp.equals(value);
            }
            default -> throw new IllegalArgumentException("Неизвестная операция: " + cond.getOperation());
        }
    }

    private boolean evaluateLuminosityCondition(Condition cond, Integer luminosity) {
        ConditionOperationAvro operation = ConditionOperationAvro.valueOf(cond.getOperation());
        int value = cond.getValue();

        switch (operation) {
            case GREATER_THAN -> {
                return luminosity > value;
            }
            case LOWER_THAN -> {
                return luminosity < value;
            }
            case EQUALS -> {
                return luminosity.equals(value);
            }
            default -> {
                log.warn("Операция {} не поддерживается для типа LUMINOSITY", operation);
                return false;
            }
        }
    }

    private boolean evaluateCo2Condition(Condition cond, Integer co2) {
        ConditionOperationAvro operation = ConditionOperationAvro.valueOf(cond.getOperation());
        int value = cond.getValue();

        switch (operation) {
            case GREATER_THAN -> {
                return co2 > value;
            }
            case LOWER_THAN -> {
                return co2 < value;
            }
            case EQUALS -> {
                return co2.equals(value);
            }
            default -> {
                log.warn("Операция {} не поддерживается для типа CO2LEVEL", operation);
                return false;
            }
        }
    }

    private boolean evaluateHumidityCondition(Condition cond, Integer humidity) {
        ConditionOperationAvro operation = ConditionOperationAvro.valueOf(cond.getOperation());
        int value = cond.getValue();

        switch (operation) {
            case GREATER_THAN -> {
                return humidity > value;
            }
            case LOWER_THAN -> {
                return humidity < value;
            }
            case EQUALS -> {
                return humidity.equals(value);
            }
            default -> {
                log.warn("Операция {} не поддерживается для типа HUMIDITY", operation);
                return false;
            }
        }
    }

    private boolean evaluateSwitchCondition(Condition cond, Boolean state) {
        ConditionOperationAvro operation = ConditionOperationAvro.valueOf(cond.getOperation());
        boolean expectedValue = Boolean.parseBoolean(cond.getValue().toString());

        switch (operation) {
            case EQUALS -> {
                return state.equals(expectedValue);
            }
            default -> {
                log.warn("Операция {} не поддерживается для типа SWITCH", operation);
                return false;
            }
        }
    }

    /**
     * Извлекает данные датчика из Avro‑структуры
     */
    private SensorDataDto extractSensorData(SensorStateAvro sensorState) {
        SensorDataDto data = new SensorDataDto();
        Object payload = sensorState.getData();

        if (payload == null) {
            return data;
        }

        try {
            if (payload instanceof ClimateSensorAvro) {
                ClimateSensorAvro climate = (ClimateSensorAvro) payload;
                data.setTemperatureC(climate.getTemperatureC());
                data.setHumidity(climate.getHumidity());
                data.setCo2Level(climate.getCo2Level());
                log.debug("Извлечены климатические данные для датчика : T={}°C, H={}%, CO₂={}ppm",
                        climate.getTemperatureC(), climate.getHumidity(), climate.getCo2Level());
            } else if (payload instanceof MotionSensorAvro) {
                MotionSensorAvro motion = (MotionSensorAvro) payload;
                data.setMotion(motion.getMotion());
                data.setVoltage(motion.getVoltage());
                log.debug("Извлечены данные движения для датчика : motion={}, voltage={}mV",
                        motion.getMotion(), motion.getVoltage());
            } else if (payload instanceof LightSensorAvro) {
                LightSensorAvro light = (LightSensorAvro) payload;
                data.setLuminosity(light.getLuminosity());
                data.setLinkQuality(light.getLinkQuality());
                log.debug("Извлечены данные освещённости для датчика : luminosity={}lux, linkQuality={}",
                        light.getLuminosity(), light.getLinkQuality());
            } else if (payload instanceof SwitchSensorAvro) {
                SwitchSensorAvro sw = (SwitchSensorAvro) payload;
                data.setState(sw.getState());
                log.debug("Извлечены данные переключателя для датчика : state={}",
                        sw.getState());
            } else if (payload instanceof TemperatureSensorAvro) {
                TemperatureSensorAvro temp = (TemperatureSensorAvro) payload;
                data.setTemperatureC(temp.getTemperatureC());
                data.setTemperatureF(temp.getTemperatureF());
                log.debug("Извлечены температурные данные для датчика : T_C={}°C, T_F={}°F",
                        temp.getTemperatureC(), temp.getTemperatureF());
            } else {
                log.warn("Неподдерживаемый тип датчика: {} (класс: {}) для датчика",
                        payload.getClass().getSimpleName(), payload.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Ошибка извлечения данных для датчика с типом {}: {}",
                    payload.getClass().getSimpleName(), e.getMessage(), e);
        }

        return data;
    }

    /**
     * Выполняет действия сценария через gRPC‑вызов
     */
    private void executeScenarioActions(Scenario scenario, String hubId) {
        Map<String, Action> actions = scenario.getActions();

        for (Map.Entry<String, Action> entry : actions.entrySet()) {
            String sensorId = entry.getKey();
            Action action = entry.getValue();

            DeviceActionProto deviceAction = convertToDeviceActionProto(sensorId, action);
            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenario.getName())
                    .setAction(deviceAction)
                    .build();

            try {
                hubRouterGrpcClient.handleDeviceAction(request);
                log.info("Выполнено действие для сценария '{}': sensor={}, action={}",
                        scenario.getName(), sensorId, action.getType());
            } catch (StatusRuntimeException e) {
                log.error("Ошибка выполнения действия для сценария '{}' на датчике {}: {}",
                        scenario.getName(), sensorId, e.getStatus());
            }
        }
    }

    private DeviceActionProto convertToDeviceActionProto(String sensorId, Action action) {
        return DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(ActionTypeProto.valueOf(action.getType()))
                .setValue(action.getValue() != null ? action.getValue() : 0)
                .build();
    }
}
