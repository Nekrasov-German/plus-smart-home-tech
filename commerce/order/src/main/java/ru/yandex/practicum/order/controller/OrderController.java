package ru.yandex.practicum.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.client_order.OrderClient;
import ru.yandex.practicum.interaction.dto_order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto_order.OrderDto;
import ru.yandex.practicum.interaction.dto_order.ProductReturnRequest;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderClient {
    private final OrderService service;

    @Override
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(@RequestParam("username") String userName) {
        return ResponseEntity.ok().body(service.getOrders(userName));
    }

    //Создание заказа
    @Override
    @PutMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateNewOrderRequest request) {
        return ResponseEntity.ok().body(service.createOrder(request));
    }

    //Возврат заказа.
    @PostMapping("/return")
    @Override
    public ResponseEntity<OrderDto> returnOrder(@RequestBody ProductReturnRequest request) {
        return ResponseEntity.ok().body(service.returnOrder(request));
    }

    //Оплата заказа.
    @PostMapping("/payment")
    @Override
    public ResponseEntity<OrderDto> paymentOrder(@RequestBody UUID orderId) {
        return ResponseEntity.ok().body(service.paymentOrder(orderId));
    }

    //Оплата заказа произошла с ошибкой.
    @PostMapping("/payment/failed")
    @Override
    public ResponseEntity<OrderDto> paymentFailed(@RequestBody UUID orderId) {
        return ResponseEntity.ok().body(service.paymentOrderFailed(orderId));
    }

    //Доставка заказа.
    @PostMapping("/delivery")
    @Override
    public ResponseEntity<OrderDto> deliveryOrder(@RequestBody UUID orderId) {
        return ResponseEntity.ok().body(service.deliveryOrder(orderId));
    }

    //Доставка заказа с ошибкой.
    @PostMapping("/delivery/failed")
    @Override
    public ResponseEntity<OrderDto> deliveryOrderFailed(@RequestBody UUID orderId) {
        return ResponseEntity.ok().body(service.paymentOrderFailed(orderId));
    }

    //Завершение заказа.
    @PostMapping("/completed")
    @Override
    public ResponseEntity<OrderDto> completedOrder(@RequestBody UUID orderId) {
        return ResponseEntity.ok().body(service.completedOrder(orderId));
    }

    //Расчёт стоимости заказа.
    @PostMapping("/calculate/total")
    @Override
    public ResponseEntity<OrderDto> calculateOrderTotal(@RequestBody UUID orderId) {
        return ResponseEntity.ok().body(service.calculateOrderTotal(orderId));
    }

    //Расчёт стоимости доставки заказа.
    @PostMapping("/calculate/delivery")
    @Override
    public ResponseEntity<OrderDto> calculateOrderDelivery(@RequestBody UUID orderId) {
        return ResponseEntity.ok().body(service.calculateOrderDelivery(orderId));
    }

    //Сборка заказа.
    @PostMapping("/assembly")
    @Override
    public ResponseEntity<OrderDto> assemblyOrder(@RequestBody UUID orderId) {
        return ResponseEntity.ok().body(service.assemblyOrder(orderId));
    }

    //Сборка заказа с ошибкой.
    @PostMapping("/assembly/failed")
    @Override
    public ResponseEntity<OrderDto> assemblyOrderFailed(@RequestBody UUID orderId) {
        return ResponseEntity.ok().body(service.assemblyOrderFailed(orderId));
    }
}
