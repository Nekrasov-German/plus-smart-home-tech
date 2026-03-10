package ru.yandex.practicum.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SensorStateDto {
    private LocalDateTime timestamp;
    private SensorDataDto data;
}
