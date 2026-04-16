package ru.yandex.practicum.interaction.client_cart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto_cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface CartClient {

    @GetMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> getCart(@RequestParam("username") String userName);

    @PutMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> addProductToCart(@RequestParam("username") String userName,
                                            @RequestBody Map<UUID, Integer> products);

    @DeleteMapping("/api/v1/shopping-cart")
    ResponseEntity<Void> deleteCart(@RequestParam("username") String userName);

    @PostMapping("/api/v1/shopping-cart/remove")
    ResponseEntity<ShoppingCartDto> deleteProduct(@RequestParam("username") String userName,
                                         @RequestBody List<String> product);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ResponseEntity<ShoppingCartDto> changeQuantity(@RequestParam("username") String userName,
                                          @RequestBody ChangeProductQuantityRequest request);
}
