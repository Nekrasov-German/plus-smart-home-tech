package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.enums.ConditionOperation;
import ru.yandex.practicum.model.enums.ConditionType;

@Getter
@Setter
@ToString
public class ScenarioCondition {

    @NotNull
    private String sensorId;

    @NotNull
    private ConditionType type;

    @NotNull
    private ConditionOperation operation;

    private int value;
}
