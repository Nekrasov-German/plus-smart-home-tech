package ru.yandex.practicum.delivery.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.delivery.model.Address;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
