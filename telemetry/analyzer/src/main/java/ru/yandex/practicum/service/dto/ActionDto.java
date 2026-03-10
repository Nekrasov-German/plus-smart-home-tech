package ru.yandex.practicum.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionDto {
    private Long id;
    private String type;   // ACTIVATE, DEACTIVATE, INVERSE, SET_VALUE
    private Integer value; // опциональное значение (например, яркость)
}
