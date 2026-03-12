package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.entity.Sensor;

import java.util.Collection;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, String> {

    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    Optional<Sensor> findByIdAndHubId(String id, String hubId);

    boolean existsById(String id);

}
