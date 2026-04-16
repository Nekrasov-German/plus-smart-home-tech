package ru.yandex.practicum.payment.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction.dto_payment.PaymentDto;
import ru.yandex.practicum.payment.model.Payment;

@UtilityClass
public class PaymentMapper {
    public PaymentDto paymentToPaymentDto(Payment payment) {
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .totalPayment(payment.getTotalPayment())
                .deliveryTotal(payment.getDeliveryTotal())
                .feeTotal(payment.getFeeTotal())
                .build();
    }
}
