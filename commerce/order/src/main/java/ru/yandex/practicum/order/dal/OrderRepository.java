package ru.yandex.practicum.order.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.order.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Order findByShoppingCardId(UUID shoppingCardId);

    List<Order> findAllByShoppingCardId(UUID shoppingCartId);
}
