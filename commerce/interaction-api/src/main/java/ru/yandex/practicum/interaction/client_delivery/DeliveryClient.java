package ru.yandex.practicum.interaction.client_delivery;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto_delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto_order.OrderDto;

import java.util.UUID;

@FeignClient(name = "delivery")
public interface DeliveryClient {
    //Создать новую доставку в БД.
    @PutMapping("/api/v1/delivery")
    ResponseEntity<DeliveryDto> createDelivery(@RequestBody DeliveryDto deliveryDto);

    //Эмуляция успешной доставки товара.
    @PostMapping("/api/v1/delivery/successful")
    ResponseEntity<Void> completeDelivery(@RequestBody UUID deliveryId);

    //Эмуляция получения товара в доставку.
    @PostMapping("/api/v1/delivery/picked")
    ResponseEntity<Void> pickedDelivery(@RequestBody UUID deliveryId);

    //Эмуляция неудачного вручения товара.
    @PostMapping("/api/v1/delivery/failed")
    ResponseEntity<Void> failedDelivery(@RequestBody UUID deliveryId);

    //Расчёт полной стоимости доставки заказа.
    @PostMapping("/api/v1/delivery/cost")
    ResponseEntity<Double> costDelivery(@RequestBody OrderDto orderDto);
}
