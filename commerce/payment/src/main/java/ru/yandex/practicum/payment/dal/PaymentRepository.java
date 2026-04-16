package ru.yandex.practicum.payment.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.payment.model.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
