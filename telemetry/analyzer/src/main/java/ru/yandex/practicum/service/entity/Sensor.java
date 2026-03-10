package ru.yandex.practicum.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    @Id
    private String id;

    private String hubId;
}
