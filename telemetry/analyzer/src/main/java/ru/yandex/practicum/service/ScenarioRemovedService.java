package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.service.entity.Scenario;

@Slf4j
@Service
@Transactional
public class ScenarioRemovedService {
    private final ScenarioRepository scenarioRepository;

    public ScenarioRemovedService(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * Обрабатывает событие удаления сценария
     * @param hubId идентификатор хаба из HubEventAvro
     * @param event событие удаления сценария
     */
    public void handle(String hubId, ScenarioRemovedEventAvro event) {
        String scenarioName = event.getName();

        log.info("Обработка удаления сценария: name={}, хаб={}", scenarioName, hubId);

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Сценарий не найден: name=%s, hub_id=%s", scenarioName, hubId)));

        scenarioRepository.delete(scenario);
        log.info("Сценарий успешно удалён: name={}", scenarioName);
    }
}
