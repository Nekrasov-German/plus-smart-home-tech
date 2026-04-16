package ru.yandex.practicum.order.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction.client_cart.CartClient;
import ru.yandex.practicum.interaction.client_delivery.DeliveryClient;
import ru.yandex.practicum.interaction.client_payment.PaymentClient;
import ru.yandex.practicum.interaction.client_warehouse.WarehouseClient;
import ru.yandex.practicum.interaction.dto_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto_delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto_order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto_order.OrderDto;
import ru.yandex.practicum.interaction.dto_order.ProductReturnRequest;
import ru.yandex.practicum.interaction.dto_payment.PaymentDto;
import ru.yandex.practicum.interaction.dto_warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto_warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.dto_warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto_warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.interaction.exception.NotAvailableServiceException;
import ru.yandex.practicum.order.dal.OrderRepository;
import ru.yandex.practicum.order.exception.*;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.model.StateOrder;
import ru.yandex.practicum.order.model.mapper.OrderMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseClient;
    private final CartClient cartClient;

    @Override
    public List<OrderDto> getOrders(String userName) {
        try {
            ShoppingCartDto cartDto = cartClient.getCart(userName).getBody();
            assert cartDto != null;
            List<Order> orders = orderRepository.findAllByShoppingCardId(cartDto.getShoppingCartId());
            return orders.stream()
                    .map(OrderMapper::orderToOrderDto)
                    .toList();
        } catch (FeignException e) {
            if (e.status() >= 400) {
                throw new NotAuthorizedUserException("Пользователь не авторизован");
            }
            return List.of();
        }
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        BookedProductsDto checked = null;
        try {
            checked = warehouseClient.checkedQuantity(request.getShoppingCart()).getBody();
            log.info("CHECKED = " + checked);
        } catch (FeignException e) {
            if (e.status() == 400) {
                throw new NoSpecifiedProductInWarehouseException(e.getMessage());
            }
        }
        if (checked == null) {
            throw new NoSpecifiedProductInWarehouseException("Сервис склада не доступен");
        }

        Order order = orderRepository.findByShoppingCardId(request.getShoppingCart().getShoppingCartId());

        if (order == null) {
            log.info("Создание нового заказа.");
            order = orderRepository.save(Order.builder()
                    .shoppingCardId(request.getShoppingCart().getShoppingCartId())
                    .products(request.getShoppingCart().getProducts())
                    .deliveryVolume(checked.getDeliveryVolume())
                    .deliveryWeight(checked.getDeliveryWeight())
                    .fragile(checked.getFragile())
                    .build());
        } else {
            log.info("Дополнение существующего заказа.");
            order.setDeliveryVolume(checked.getDeliveryVolume());
            order.setDeliveryWeight(checked.getDeliveryWeight());
            order.setFragile(checked.getFragile());
        }

        try {
            AddressDto warehouseAddress = warehouseClient.getAddress().getBody();

            DeliveryDto deliveryDto = deliveryClient.createDelivery(DeliveryDto.builder()
                    .orderId(order.getOrderId())
                    .fromAddress(warehouseAddress)
                    .toAddress(request.getDeliveryAddress())
                    .build()).getBody();

            order.setDeliveryId(deliveryDto.getDeliveryId());

            orderRepository.save(order);
        } catch (FeignException e) {
            if (e.status() >= 400) {
                throw new NotAvailableServiceException("Сервис склада или доставки не доступен.");
            }
        }

        try {
            Double totalPriceProduct = paymentClient.productCostPayment(OrderMapper.orderToOrderDto(order)).getBody();
            order.setProductPrice(totalPriceProduct);
            orderRepository.save(order);
        } catch (FeignException e) {
            if (e.status() >= 400) {
                throw new NotAvailableServiceException("Сервис платежей не доступен.");
            }
        }

        return OrderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
        Map<UUID, Integer> products = request.getProducts();
        Map<UUID, Integer> orderProducts = order.getProducts();
        for (UUID idProduct : products.keySet()) {
            if (orderProducts.containsKey(idProduct)) {
                if (orderProducts.get(idProduct).equals(products.get(idProduct))) {
                    orderProducts.remove(idProduct);
                } else {
                    Integer quantity = orderProducts.get(idProduct) - products.get(idProduct);
                    orderProducts.put(idProduct, quantity);
                }
            } else {
                throw new NoOrderFoundException("Такого продукта в заказе нет {}" + idProduct);
            }
        }
        order.setProducts(orderProducts);
        order.setState(StateOrder.PRODUCT_RETURNED);
        orderRepository.save(order);

        try {
            warehouseClient.returnProduct(products);
        } catch (FeignException e) {
            if (e.status() >= 400) {
                throw new NotAvailableServiceException("Сервис склада не доступен.");
            }
        }

        return OrderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
        if (order.getState() == StateOrder.ON_PAYMENT) {
            try {
                paymentClient.refundPayment(order.getPaymentId());
                order.setState(StateOrder.PAID);
            } catch (FeignException e) {
                if (e.status() >= 400) {
                    throw new NotAvailableServiceException("Сервис платежей не доступен.");
                }
            }
        }
        return OrderMapper.orderToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto paymentOrderFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
        if (order.getState() == StateOrder.ON_PAYMENT) {
            try {
                paymentClient.failedPayment(order.getPaymentId());
                order.setState(StateOrder.PAYMENT_FAILED);
            } catch (FeignException e) {
                if (e.status() >= 400) {
                    throw new NotAvailableServiceException("Сервис платежей не доступен.");
                }
            }
        }
        return OrderMapper.orderToOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        if (order.getState() == StateOrder.ASSEMBLED) {
            try {
                deliveryClient.completeDelivery(order.getDeliveryId());
                order.setState(StateOrder.DELIVERED);
                orderRepository.save(order);
            } catch (FeignException e) {
                if (e.status() >= 400) {
                    throw new NotAvailableServiceException("Сервис доставки не доступен.");
                }
            }
        } else {
            throw new NotAssembledOrderException("Товар не собран на складе.");
        }
        return OrderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto deliveryOrderFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        if (order.getState() == StateOrder.ASSEMBLED) {
            try {
                deliveryClient.failedDelivery(order.getDeliveryId());
                order.setState(StateOrder.DELIVERY_FAILED);
                orderRepository.save(order);
            } catch (FeignException e) {
                if (e.status() >= 400) {
                    throw new NotAvailableServiceException("Сервис доставки не доступен.");
                }
            }
        } else {
            throw new NotAssembledOrderException("Товар не собран на складе.");
        }
        return OrderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto completedOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        if (order.getState() == StateOrder.DELIVERED) {
            order.setState(StateOrder.DONE);
            orderRepository.save(order);
        } else {
            throw new NotAssembledOrderException("Товар не доставлен.");
        }
        return OrderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto calculateOrderTotal(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        try {
            Double totalPrice = paymentClient.totalCostPayment(OrderMapper.orderToOrderDto(order)).getBody();
            order.setTotalPrice(totalPrice);
            orderRepository.save(order);
        } catch (FeignException e) {
            if (e.status() >= 400) {
                throw new NotAvailableServiceException("Сервис платежей не доступен.");
            }
        }

        try {
            PaymentDto paymentDto = paymentClient.formPayment(OrderMapper.orderToOrderDto(order)).getBody();
            assert paymentDto != null;
            order.setPaymentId(paymentDto.getPaymentId());
            order.setState(StateOrder.ON_PAYMENT);
            orderRepository.save(order);
        } catch (FeignException e) {
            if (e.status() >= 400) {
                throw new NotAvailableServiceException("Сервис платежей не доступен.");
            }
        }

        return OrderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto calculateOrderDelivery(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        try {
            Double costDelivery = deliveryClient.costDelivery(OrderMapper.orderToOrderDto(order)).getBody();

            order.setDeliveryPrice(costDelivery);

            order = orderRepository.save(order);
        } catch (FeignException e) {
            if (e.status() >= 400) {
                throw new NotAvailableServiceException("Сервис доставки не доступен.");
            }
        }
        return OrderMapper.orderToOrderDto(order);
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        if (order.getState() == StateOrder.PAID) {
            try {
                AssemblyProductsForOrderRequest request = AssemblyProductsForOrderRequest.builder()
                        .orderId(orderId)
                        .products(order.getProducts())
                        .build();
                warehouseClient.assembly(request);
                warehouseClient.shipped(ShippedToDeliveryRequest.builder()
                        .deliveryId(order.getDeliveryId())
                        .orderId(order.getOrderId())
                        .build());
                order.setState(StateOrder.ASSEMBLED);
            } catch (FeignException e) {
                if (e.status() >= 400) {
                    assemblyOrderFailed(orderId);
                    throw new NotAvailableServiceException("Ошибка сборки заказа.");
                }
            }
            return OrderMapper.orderToOrderDto(orderRepository.save(order));
        } else {
            throw new NotPaymentException("Товар не оплачен.");
        }
    }

    @Override
    public OrderDto assemblyOrderFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        if (order.getState() == StateOrder.PAID) {
            order.setState(StateOrder.ASSEMBLY_FAILED);
        } else {
            throw new NotPaymentException("Товар не оплачен.");
        }
        return OrderMapper.orderToOrderDto(orderRepository.save(order));
    }
}
