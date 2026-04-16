package ru.yandex.practicum.interaction.client_order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto_order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto_order.OrderDto;
import ru.yandex.practicum.interaction.dto_order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order")
public interface OrderClient {

    //Получить заказы пользователя.
    @GetMapping("/api/v1/order")
    ResponseEntity<List<OrderDto>> getOrders(@RequestParam("username") String userName);

    //Создать новый заказ в системе.
    @PutMapping("/api/v1/order")
    ResponseEntity<OrderDto> createOrder(@RequestBody CreateNewOrderRequest request);

    //Возврат заказа.
    @PostMapping("/api/v1/order/return")
    ResponseEntity<OrderDto> returnOrder(@RequestBody ProductReturnRequest request);

    //Оплата заказа.
    @PostMapping("/api/v1/order/payment")
    ResponseEntity<OrderDto> paymentOrder(@RequestBody UUID orderId);

    //Оплата заказа произошла с ошибкой.
    @PostMapping("/api/v1/order/payment/failed")
    ResponseEntity<OrderDto> paymentFailed(@RequestBody UUID orderId);

    //Доставка заказа.
    @PostMapping("/api/v1/order/delivery")
    ResponseEntity<OrderDto> deliveryOrder(@RequestBody UUID orderId);

    //Доставка заказа с ошибкой.
    @PostMapping("/api/v1/order/delivery/failed")
    ResponseEntity<OrderDto> deliveryOrderFailed(@RequestBody UUID orderId);

    //Завершение заказа.
    @PostMapping("/api/v1/order/completed")
    ResponseEntity<OrderDto> completedOrder(@RequestBody UUID orderId);

    //Расчёт стоимости заказа.
    @PostMapping("/api/v1/order/calculate/total")
    ResponseEntity<OrderDto> calculateOrderTotal(@RequestBody UUID orderId);

    //Расчёт стоимости доставки заказа.
    @PostMapping("/api/v1/order/calculate/delivery")
    ResponseEntity<OrderDto> calculateOrderDelivery(@RequestBody UUID orderId);

    //Сборка заказа.
    @PostMapping("/api/v1/order/assembly")
    ResponseEntity<OrderDto> assemblyOrder(@RequestBody UUID orderId);

    //Сборка заказа с ошибкой.
    @PostMapping("/api/v1/order/assembly/failed")
    ResponseEntity<OrderDto> assemblyOrderFailed(@RequestBody UUID orderId);
}
