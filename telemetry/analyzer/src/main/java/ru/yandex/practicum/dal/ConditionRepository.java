package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.entity.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
