package ru.yandex.practicum.interaction.client_payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto_order.OrderDto;
import ru.yandex.practicum.interaction.dto_payment.PaymentDto;

import java.util.UUID;

@FeignClient(name = "payment")
public interface PaymentClient {
    //Формирование оплаты для заказа (переход в платежный шлюз).
    @PostMapping("/api/v1/payment")
    ResponseEntity<PaymentDto> formPayment(@RequestBody OrderDto orderDto);

    //Расчёт полной стоимости заказа.
    @PostMapping("/api/v1/payment/totalCost")
    ResponseEntity<Double> totalCostPayment(@RequestBody OrderDto orderDto);

    //Метод для эмуляции успешной оплаты в платежного шлюза.
    @PostMapping("/api/v1/payment/refund")
    ResponseEntity<Void> refundPayment(@RequestBody UUID paymentId);

    //Расчёт стоимости товаров в заказе.
    @PostMapping("/api/v1/payment/productCost")
    ResponseEntity<Double> productCostPayment(@RequestBody OrderDto orderDto);

    //Метод для эмуляции отказа в оплате платежного шлюза.
    @PostMapping("/api/v1/payment/failed")
    ResponseEntity<Void> failedPayment(@RequestBody UUID paymentId);
}
