package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import ru.yandex.practicum.interaction.dto_store.ProductDto;
import ru.yandex.practicum.model.Product;

import java.util.List;
import java.util.UUID;

public interface StoreService {
    //Получение страницы товаров указанной категории
    Page<Product> getPage(String category, Integer page, Integer size, List<String> sort);

    //Создание нового товара в ассортименте
    ProductDto addProduct(ProductDto productDto);

    //Обновление товара в ассортименте, например уточнение описания, характеристик и т.д.
    ProductDto updateProduct(ProductDto productDto);

    //Удалить товар из ассортимента магазина. Функция для менеджерского состава.
    Boolean removeProduct(UUID id);

    //Установка статуса по товару. API вызывается со стороны склада.
    Boolean quantityState(UUID productId, String quantityState);

    //Получить сведения по товару из БД.
    ProductDto getInfoProduct(UUID id);
}
