package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.entity.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
