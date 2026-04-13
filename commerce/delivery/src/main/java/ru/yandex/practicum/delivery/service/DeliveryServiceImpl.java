package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.delivery.dal.AddressRepository;
import ru.yandex.practicum.delivery.dal.DeliveryRepository;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.model.State;
import ru.yandex.practicum.delivery.model.mapper.DeliveryMapper;
import ru.yandex.practicum.interaction.client_order.OrderClient;
import ru.yandex.practicum.interaction.client_warehouse.WarehouseClient;
import ru.yandex.practicum.interaction.dto_delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto_order.OrderDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final static double BASE_RATE = 5.0;
    private final static double VOLUME_RATE = 0.2;
    private final static double WEIGHT_RATE = 0.3;
    private final static double FRAGILE_RATE = 0.2;
    private final static double ANOTHER_ADDRESS_RATE = 0.2;

    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {

        Address toAddress = addressRepository.save(DeliveryMapper.addressDtoToAddress(deliveryDto.getToAddress()));
        Address fromAddress = addressRepository.save(DeliveryMapper.addressDtoToAddress(deliveryDto.getFromAddress()));

        Delivery delivery = deliveryRepository.save(Delivery.builder()
                .orderId(deliveryDto.getOrderId())
                .toAddressId(toAddress.getAddressId())
                .fromAddressId(fromAddress.getAddressId())
                .deliveryState(State.CREATED)
                .build());

        return DeliveryMapper.deliveryToDeliveryDto(delivery, toAddress, fromAddress);
    }

    @Override
    public void completeDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена."));
        delivery.setDeliveryState(State.DELIVERED);
        deliveryRepository.save(delivery);
    }

    @Override
    public void pickedDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена."));
        delivery.setDeliveryState(State.IN_PROGRESS);
        deliveryRepository.save(delivery);
    }

    @Override
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена."));
        delivery.setDeliveryState(State.FAILED);
        deliveryRepository.save(delivery);
    }

    @Override
    public Double costDelivery(OrderDto orderDto) {
        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException(orderDto.getDeliveryId().toString()));
        double result = BASE_RATE;
        //Если адрес склада содержит название ADDRESS_1, то умножаем на 1.
        //Если адрес склада содержит название ADDRESS_2, то умножаем на 2.
        if (delivery.getFromAddress(addressRepository).getStreet().equals("ADDRESS_1")) {
            result = result + (BASE_RATE * 1);
        } else if (delivery.getFromAddress(addressRepository).getStreet().equals("ADDRESS_2")) {
            result = result + (BASE_RATE * 2);
        }
        //Если в заказе есть признак хрупкости, умножаем сумму на 0.2
        if (orderDto.getFragile()) {
            result += result * FRAGILE_RATE;
        }
        //Добавляем к сумме, полученной на предыдущих шагах, вес заказа, умноженный на 0.3.
        result += orderDto.getDeliveryWeight() * WEIGHT_RATE;

        //Складываем с полученным на прошлом шаге итогом объём, умноженный на 0.2.
        result += orderDto.getDeliveryVolume() * VOLUME_RATE;

        //Улица доставки совпадает с адресом склада, Иначе её нужно умножить на 0.2 и сложить с итогом.
        if (!delivery.getFromAddress(addressRepository).equals(delivery.getToAddress(addressRepository))) {
            result += result * ANOTHER_ADDRESS_RATE;
        }

        return result;
    }
}
