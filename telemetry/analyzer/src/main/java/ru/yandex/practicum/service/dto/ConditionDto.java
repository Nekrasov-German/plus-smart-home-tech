package ru.yandex.practicum.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionDto {
    private Long id;
    private String type;      // MOTION, TEMPERATURE, LUMINOSITY и т. д.
    private String operation;   // EQUALS, GREATER_THAN, LOWER_THAN
    private Integer value;    // значение для сравнения
}
