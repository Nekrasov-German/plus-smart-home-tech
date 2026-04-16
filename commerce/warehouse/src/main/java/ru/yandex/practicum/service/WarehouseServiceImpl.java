package ru.yandex.practicum.service;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.ProductWarehouseRepository;
import ru.yandex.practicum.interaction.client_delivery.DeliveryClient;
import ru.yandex.practicum.interaction.dto_warehouse.*;
import ru.yandex.practicum.interaction.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.exception.NotAvailableServiceException;
import ru.yandex.practicum.interaction.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.ProductWarehouse;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    private final ProductWarehouseRepository productRepository;
    private final DeliveryClient deliveryClient;

    @Override
    @Transactional
    public void addProduct(NewProductInWarehouseRequest request) {
        if (productRepository.findById(request.getProductId()).isPresent()) {
            throw new SpecifiedProductAlreadyInWarehouseException(request.getProductId());
        }

        // Создаём Dimension из DTO
        DimensionDto dimensionDto = request.getDimension();
        Dimension dimension = Dimension.builder()
                .width(dimensionDto.getWidth())
                .height(dimensionDto.getHeight())
                .depth(dimensionDto.getDepth())
                .build();

        // Создаём ProductWarehouse со встроенным Dimension
        ProductWarehouse product = ProductWarehouse.builder()
                .productId(request.getProductId())
                .fragile(request.getFragile())
                .dimension(dimension)
                .weight(request.getWeight())
                .build();

        ProductWarehouse productWarehouse = productRepository.save(product);
        log.info("ТОВАР ДОБАВЛЕН НА СКЛАД: " + productWarehouse.toString());
    }

    @Override
    public BookedProductsDto checkedQuantity(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Integer> products = shoppingCartDto.getProducts();
        double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;

        for (UUID id : products.keySet()) {
            Integer quantity = products.get(id);
            log.info("Товар: " + id + ", количество: " + quantity);
            ProductWarehouse productWarehouse = productRepository.findById(id)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(id));
            if (productWarehouse.getQuantity() < quantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(id);
            }

            deliveryWeight += productWarehouse.getWeight() * quantity;
            deliveryVolume += productWarehouse.getDimension().getVolume() * quantity;
            if (productWarehouse.getFragile()) {
                fragile = true;
            }
        }

        log.info("Общий объем {} в куб.м Общий вес {} в кг", deliveryVolume, deliveryWeight);

        return BookedProductsDto.builder()
                .deliveryVolume(deliveryVolume)
                .deliveryWeight(deliveryWeight)
                .fragile(fragile)
                .build();
    }

    @Override
    public void addProductToWarehouse(UUID productId, Integer quantityToAdd) {
        ProductWarehouse productWarehouse = productRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(productId));
        productWarehouse.setQuantity(quantityToAdd);
        ProductWarehouse productWarehouseQuantity = productRepository.save(productWarehouse);
        log.info("ТОВАР {} ОБНОВЛЕНО КОЛЛИЧЕСТВО : {}", productWarehouseQuantity.getProductId(),
                productWarehouseQuantity.getQuantity());
    }

    @Override
    public AddressDto getAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Override
    public void assembly(AssemblyProductsForOrderRequest request) {
        Map<UUID, Integer> products = request.getProducts();
        for (UUID id : products.keySet()) {
            Integer quantity = products.get(id);

            log.info("Сборка товара {} в количестве :{}", id, quantity);

            ProductWarehouse productWarehouse = productRepository.findById(id)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(id));

            addProductToWarehouse(productWarehouse.getProductId(), productWarehouse.getQuantity() - quantity);
        }
    }

    @Override
    public void shipped(ShippedToDeliveryRequest request) {
        try {
            deliveryClient.pickedDelivery(request.getDeliveryId());
        } catch (FeignException e) {
            if (e.status() >= 400) {
                throw new NotAvailableServiceException("Ошибка сборки товара.");
            }
        }
    }

    @Override
    public void returnProduct(Map<UUID, Integer> products) {
        for (UUID id : products.keySet()) {
            Integer quantity = products.get(id);

            log.info("Возврат товара {} в количестве :{}", id, quantity);

            ProductWarehouse productWarehouse = productRepository.findById(id)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(id));

            addProductToWarehouse(productWarehouse.getProductId(), productWarehouse.getQuantity() + quantity);
        }
    }
}
