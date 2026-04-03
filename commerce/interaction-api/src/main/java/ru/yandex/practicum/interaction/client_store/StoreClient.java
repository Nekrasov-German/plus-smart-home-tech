package ru.yandex.practicum.interaction.client_store;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto_store.PageProductDto;
import ru.yandex.practicum.interaction.dto_store.ProductDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface StoreClient {

    @GetMapping
    ResponseEntity<PageProductDto> getPage(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) List<String> sort);

    @PutMapping
    ResponseEntity<ProductDto> putProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping
    ResponseEntity<ProductDto> postProduct(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    ResponseEntity<Object> removeProduct(@RequestBody UUID id);

    @PostMapping("/quantityState")
    ResponseEntity<Object> quantityState(@RequestParam UUID productId,
                                                @RequestParam String quantityState);

    @GetMapping("/{productId}")
    ResponseEntity<ProductDto> getProduct(@PathVariable("productId") UUID productId);
}
