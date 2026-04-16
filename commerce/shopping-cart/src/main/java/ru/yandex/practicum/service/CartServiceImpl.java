package ru.yandex.practicum.service;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dal.CartRepository;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ProductNotEnoughWarehouseException;
import ru.yandex.practicum.exception.ServiceUnavailableException;
import ru.yandex.practicum.interaction.client_warehouse.WarehouseClient;
import ru.yandex.practicum.interaction.dto_cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto_warehouse.BookedProductsDto;
import ru.yandex.practicum.model.CartState;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.mapper.CartMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository repository;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getCart(String userName) {
        if (userName.isEmpty()) {
            throw new NotAuthorizedUserException(userName);
        }

        Optional<ShoppingCart> cart = repository.findByUserName(userName);
        if (cart.isEmpty()) {
            ShoppingCart emptyCart = repository.save(ShoppingCart.builder()
                    .userName(userName)
                    .cartState(CartState.ACTIVE)
                    .build());
            return CartMapper.shoppingCartToShoppingCartDto(emptyCart);
        }
        return CartMapper.shoppingCartToShoppingCartDto(cart.get());
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToCart(String userName, Map<UUID, Integer> products) {
        if (userName.isEmpty()) {
            throw new NotAuthorizedUserException(userName);
        }
        Optional<ShoppingCart> cartUser = repository.findByUserName(userName);
        if (cartUser.isEmpty()) {
            ShoppingCart emptyCart = repository.save(ShoppingCart.builder()
                    .userName(userName)
                    .products(products)
                    .cartState(CartState.ACTIVE)
                    .build());
            try {
                BookedProductsDto checked = warehouseClient.checkedQuantity(
                        CartMapper.shoppingCartToShoppingCartDto(emptyCart)).getBody();
            } catch (FeignException e) {
                if (e.status() >= 400 && e.status() < 500) {
                    throw new ProductNotEnoughWarehouseException(e.getMessage());
                } else if (e.status() >= 500) {
                    throw new ServiceUnavailableException(e.getMessage());
                }
            }

            return CartMapper.shoppingCartToShoppingCartDto(repository.save(emptyCart));
        }

        Map<UUID, Integer> cartProduct = cartUser.get().getProducts();
        for (UUID id : products.keySet()) {
            if (cartProduct.containsKey(id)) {
                cartProduct.put(id, cartProduct.get(id) + products.get(id));
            } else {
                cartProduct.put(id, products.get(id));
            }
        }
        cartUser.get().setProducts(cartProduct);

        try {
            BookedProductsDto checked = warehouseClient.checkedQuantity(
                    CartMapper.shoppingCartToShoppingCartDto(cartUser.get())).getBody();
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                throw new ProductNotEnoughWarehouseException(e.getMessage());
            } else if (e.status() >= 500) {
                throw new ServiceUnavailableException(e.getMessage());
            }
        }

        ShoppingCart cart = repository.save(cartUser.get());
        return CartMapper.shoppingCartToShoppingCartDto(cart);
    }

    @Override
    public void detectedCart(String userName) {
        if (userName.isEmpty()) {
            throw new NotAuthorizedUserException(userName);
        }

        ShoppingCart cartUser = repository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Корзина для пользователя " + userName + " не найдена"));
        cartUser.setCartState(CartState.DEACTIVATE);
        repository.save(cartUser);
    }

    @Override
    public ShoppingCartDto deleteProduct(String userName, List<String> product) {
        if (userName.isEmpty()) {
            throw new NotAuthorizedUserException(userName);
        }

        ShoppingCart cartUser = repository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Корзина для пользователя " + userName + " не найдена"));
        Map<UUID, Integer> products = cartUser.getProducts();
        product.stream()
                .filter(products::containsKey)
                .forEach(products::remove);
        cartUser.setProducts(products);

        return CartMapper.shoppingCartToShoppingCartDto(repository.save(cartUser));
    }

    @Override
    public ShoppingCartDto changeQuantity(String userName, ChangeProductQuantityRequest request) {
        if (userName.isEmpty()) {
            throw new NotAuthorizedUserException(userName);
        }

        ShoppingCart cartUser = repository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Корзина для пользователя " + userName + " не найдена"));
        Map<UUID, Integer> products = cartUser.getProducts();

        if (products.containsKey(request.getProductId())) {
            products.put(request.getProductId(), request.getNewQuantity());
        } else {
            throw new NoProductsInShoppingCartException(request.getProductId());
        }
        cartUser.setProducts(products);
        repository.save(cartUser);
        return CartMapper.shoppingCartToShoppingCartDto(repository.save(cartUser));
    }
}
