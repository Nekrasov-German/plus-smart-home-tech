package ru.yandex.practicum.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorsSnapshotDto {
    private String hubId;
    private LocalDateTime timestamp;
    private Map<String, SensorStateDto> sensorsState;
}
