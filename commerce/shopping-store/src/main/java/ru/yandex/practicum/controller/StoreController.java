package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.client_store.StoreClient;
import ru.yandex.practicum.interaction.dto_store.PageProductDto;
import ru.yandex.practicum.interaction.dto_store.ProductDto;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.mapper.ProductMapper;
import ru.yandex.practicum.service.StoreService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class StoreController implements StoreClient {

    private final StoreService service;

    //Получение страницы товаров указанной категории
    @GetMapping
    public ResponseEntity<PageProductDto> getPage(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) List<String> sort) {

        Page<Product> productPage = service.getPage(category, page, size, sort);

        return ResponseEntity.ok().body(ProductMapper.convertToPageProductDto(productPage));
    }

    //Создание нового товара в ассортименте
    @PutMapping
    public ResponseEntity<ProductDto> putProduct(@RequestBody @Valid ProductDto productDto) {
        return ResponseEntity.ok().body(service.addProduct(productDto));
    }

    //Обновление товара в ассортименте, например уточнение описания, характеристик и т.д.
    @PostMapping
    public ResponseEntity<ProductDto> postProduct(@RequestBody @Valid ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.updateProduct(productDto));
    }

    //Удалить товар из ассортимента магазина. Функция для менеджерского состава.
    @PostMapping("/removeProductFromStore")
    public ResponseEntity<Object> removeProduct(@RequestBody UUID id) {
        return ResponseEntity.ok(service.removeProduct(id));
    }

    //Установка статуса по товару. API вызывается со стороны склада.
    //Индентификатор товара передавать в URL и статус
    @PostMapping("/quantityState")
    public ResponseEntity<Object> quantityState(@RequestParam UUID productId,
                                                @RequestParam String quantityState) {
        return ResponseEntity.ok(service.quantityState(productId, quantityState));
    }

    //Получить сведения по товару из БД.
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("productId") UUID productId) {
        return ResponseEntity.ok().body(service.getInfoProduct(productId));
    }
}
