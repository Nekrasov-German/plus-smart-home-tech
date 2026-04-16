package ru.yandex.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.interaction.client_payment.PaymentClient;
import ru.yandex.practicum.interaction.dto_order.OrderDto;
import ru.yandex.practicum.interaction.dto_payment.PaymentDto;
import ru.yandex.practicum.payment.service.PaymentService;

import java.util.UUID;

@Controller
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentClient {
    private final PaymentService paymentService;

    //Формирование оплаты для заказа (переход в платежный шлюз).
    @Override
    @PostMapping
    public ResponseEntity<PaymentDto> formPayment(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok().body(paymentService.formPayment(orderDto));
    }
    //Расчёт полной стоимости заказа.
    @Override
    @PostMapping("/totalCost")
    public ResponseEntity<Double> totalCostPayment(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok().body(paymentService.totalCostPayment(orderDto));
    }
    //Метод для эмуляции успешной оплаты в платежного шлюза.
    @Override
    @PostMapping("/refund")
    public ResponseEntity<Void> refundPayment(@RequestBody UUID paymentId) {
        paymentService.refundPayment(paymentId);
        return ResponseEntity.ok().body(null);
    }
    //Расчёт стоимости товаров в заказе.
    @Override
    @PostMapping("/productCost")
    public ResponseEntity<Double> productCostPayment(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok().body(paymentService.productCostPayment(orderDto));
    }
    //Метод для эмуляции отказа в оплате платежного шлюза.
    @Override
    @PostMapping("/failed")
    public ResponseEntity<Void> failedPayment(@RequestBody UUID paymentId) {
        paymentService.failedPayment(paymentId);
        return ResponseEntity.ok().body(null);
    }
}
