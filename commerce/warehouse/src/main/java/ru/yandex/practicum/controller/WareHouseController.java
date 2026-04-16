package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.client_warehouse.WarehouseClient;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto_warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WareHouseController implements WarehouseClient {
    private final WarehouseService service;

    //Добавить новый товар на склад
    @PutMapping
    public ResponseEntity<Void> addProduct(@RequestBody @Valid NewProductInWarehouseRequest request) {
        service.addProduct(request);
        return ResponseEntity.ok().body(null);
    }

    //Предварительно проверить что количество товаров на складе достаточно для данной корзиный продуктов
    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkedQuantity(@RequestBody ShoppingCartDto shoppingCartDto) {
        return ResponseEntity.ok().body(service.checkedQuantity(shoppingCartDto));
    }

    //Принять товар на склад.
    @PostMapping("/add")
    public ResponseEntity<Void> addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request) {
        service.addProductToWarehouse(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    //Предоставить адрес склада для расчёта доставки
    @GetMapping("/address")
    public ResponseEntity<AddressDto> getAddress() {
        return ResponseEntity.ok().body(service.getAddress());
    }

    //Собрать товары к заказу для подготовки к отправке.
    @PostMapping("/assembly")
    public ResponseEntity<Void> assembly(@RequestBody AssemblyProductsForOrderRequest request) {
        service.assembly(request);
        return ResponseEntity.ok().body(null);
    }
    //Передать товары в доставку.
    @PostMapping("/shipped")
    public ResponseEntity<Void> shipped(@RequestBody ShippedToDeliveryRequest request) {
        service.shipped(request);
        return ResponseEntity.ok().body(null);
    }
    //Принять возврат товаров на склад.
    @PostMapping("/return")
    public ResponseEntity<Void> returnProduct(@RequestBody Map<UUID, Integer> products) {
        return null;
    }
}
