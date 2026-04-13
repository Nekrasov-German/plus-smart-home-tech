package ru.yandex.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.interaction.client_delivery.DeliveryClient;
import ru.yandex.practicum.interaction.dto_delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto_order.OrderDto;

import java.util.UUID;

@Controller
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryClient {

    private final DeliveryService deliveryService;
    //Создать новую доставку в БД.
    @Override
    @PutMapping
    public ResponseEntity<DeliveryDto> createDelivery(@RequestBody DeliveryDto deliveryDto) {
        return ResponseEntity.ok().body(deliveryService.createDelivery(deliveryDto));
    }
    //Эмуляция успешной доставки товара.
    @Override
    @PostMapping("/successful")
    public ResponseEntity<Void> completeDelivery(@RequestBody UUID deliveryId) {
        deliveryService.completeDelivery(deliveryId);
        return ResponseEntity.ok().body(null);
    }
    //Эмуляция получения товара в доставку.
    @Override
    @PostMapping("/picked")
    public ResponseEntity<Void> pickedDelivery(@RequestBody UUID deliveryId) {
        deliveryService.pickedDelivery(deliveryId);
        return ResponseEntity.ok().body(null);
    }
    //Эмуляция неудачного вручения товара.
    @Override
    @PostMapping("/failed")
    public ResponseEntity<Void> failedDelivery(@RequestBody UUID deliveryId) {
        deliveryService.failedDelivery(deliveryId);
        return ResponseEntity.ok().body(null);
    }
    //Расчёт полной стоимости доставки заказа.
    @Override
    @PostMapping("/cost")
    public ResponseEntity<Double> costDelivery(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok().body(deliveryService.costDelivery(orderDto));
    }
}
