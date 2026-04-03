package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.client_cart.CartClient;
import ru.yandex.practicum.interaction.dto_cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class CartController implements CartClient {
    private final CartService service;

    //Получить актуальную корзину для авторизованного пользователя
    @GetMapping
    public ResponseEntity<ShoppingCartDto> getCart(@RequestParam("username") String userName) {
        return ResponseEntity.ok(service.getCart(userName));
    }

    //Добавить товар в корзину.
    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProductToCart(@RequestParam("username") String userName,
                                            @RequestBody Map<UUID, Integer> products) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addProductToCart(userName, products));
    }

    //Деактивация корзины товаров для пользователя.
    @DeleteMapping
    public ResponseEntity<Void> deleteCart(@RequestParam("username") String userName) {
        service.detectedCart(userName);
        return ResponseEntity.ok(null);
    }

    //Удалить указанные товары из корзины пользователя
    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> deleteProduct(@RequestParam("username") String userName,
                                         @RequestBody List<String> product) {
        return ResponseEntity.ok().body(service.deleteProduct(userName, product));
    }

    //Изменить количество товаров в корзине
    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeQuantity(@RequestParam("username") String userName,
                                          @RequestBody @Valid ChangeProductQuantityRequest request) {
        return ResponseEntity.ok().body(service.changeQuantity(userName, request));
    }
}
