package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.ProductWarehouseRepository;
import ru.yandex.practicum.interaction.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto_warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto_warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto_warehouse.DimensionDto;
import ru.yandex.practicum.interaction.dto_warehouse.NewProductInWarehouseRequest;
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

        productRepository.save(product);
    }

    @Override
    public BookedProductsDto checkedQuantity(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Integer> products = shoppingCartDto.getProducts();
        double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();
            System.out.println("Товар: " + productId + ", количество: " + quantity);
            ProductWarehouse productWarehouse = productRepository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(productId));
            if (productWarehouse.getQuantity() < quantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(productId);
            }
            deliveryWeight += productWarehouse.getWeight();
            deliveryVolume += productWarehouse.getDimension().getVolume();
            if (productWarehouse.getFragile()) {
                fragile = true;
            }
        }

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
        productRepository.save(productWarehouse);
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
}
