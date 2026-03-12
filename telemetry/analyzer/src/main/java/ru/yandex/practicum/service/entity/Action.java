package ru.yandex.practicum.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "actions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorId;
    private String type;
    private Integer value;
}
