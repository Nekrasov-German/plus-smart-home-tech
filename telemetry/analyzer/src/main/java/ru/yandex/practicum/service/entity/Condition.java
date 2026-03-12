package ru.yandex.practicum.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorId;
    private String type;
    private String operation;
    private Integer value;
}
