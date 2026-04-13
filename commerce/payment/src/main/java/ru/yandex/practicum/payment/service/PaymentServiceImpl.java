package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction.client_order.OrderClient;
import ru.yandex.practicum.interaction.client_store.StoreClient;
import ru.yandex.practicum.interaction.dto_order.OrderDto;
import ru.yandex.practicum.interaction.dto_payment.PaymentDto;
import ru.yandex.practicum.interaction.dto_store.ProductDto;
import ru.yandex.practicum.payment.dal.PaymentRepository;
import ru.yandex.practicum.payment.exception.NoOrderFoundException;
import ru.yandex.practicum.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.model.PaymentState;
import ru.yandex.practicum.payment.model.mapper.PaymentMapper;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final Double NDS = 0.1;

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final StoreClient storeClient;

    @Override
    public PaymentDto formPayment(OrderDto orderDto) {
        if (orderDto.getProductPrice() != null &&
                orderDto.getDeliveryPrice() != null &&
                orderDto.getTotalPrice() != null) {
            Payment payment = Payment.builder()
                    .totalPayment(orderDto.getProductPrice())
                    .deliveryTotal(orderDto.getDeliveryPrice())
                    .feeTotal(orderDto.getTotalPrice())
                    .build();
            return PaymentMapper.paymentToPaymentDto(paymentRepository.save(payment));
        } else {
            throw new NotEnoughInfoInOrderToCalculateException("Не достаточно информации для формирования платежа.");
        }
    }

    @Override
    public Double totalCostPayment(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null || orderDto.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не достаточно информации для расчёта платежа.");
        }
        return orderDto.getDeliveryPrice() + (orderDto.getProductPrice() * NDS);
    }

    @Override
    public void refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Платежка не найдена"));
        payment.setPaymentState(PaymentState.SUCCESS);
    }

    @Override
    public Double productCostPayment(OrderDto orderDto) {
        Map<UUID, Integer> products = orderDto.getProducts();
        Double totalPriceProduct = 0.0;
        for (UUID id : products.keySet()) {
            ProductDto productDto = storeClient.getProduct(id).getBody();
            assert productDto != null;
            totalPriceProduct += productDto.getPrice() * products.get(id);
        }
        return totalPriceProduct;
    }

    @Override
    public void failedPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Платежка не найдена"));
        payment.setPaymentState(PaymentState.FAILED);
    }
}
