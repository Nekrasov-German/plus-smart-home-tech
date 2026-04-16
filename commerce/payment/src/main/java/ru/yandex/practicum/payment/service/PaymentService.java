package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.interaction.dto_order.OrderDto;
import ru.yandex.practicum.interaction.dto_payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {

    PaymentDto formPayment(OrderDto orderDto);

    Double totalCostPayment(OrderDto orderDto);

    void refundPayment(UUID paymentId);

    Double productCostPayment(OrderDto orderDto);

    void failedPayment(UUID paymentId);
}
