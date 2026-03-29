package ru.yandex.practicum.interaction.client_warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto_warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto_warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto_warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto_warehouse.NewProductInWarehouseRequest;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @PutMapping("/api/v1/warehouse")
    ResponseEntity<Void> addProduct(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/check")
    ResponseEntity<BookedProductsDto> checkedQuantity(@RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/api/v1/warehouse/add")
    ResponseEntity<Void> addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/api/v1/warehouse/address")
    ResponseEntity<AddressDto> getAddress();
}
