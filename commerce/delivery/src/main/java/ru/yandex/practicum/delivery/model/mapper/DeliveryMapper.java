package ru.yandex.practicum.delivery.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.interaction.dto_delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto_delivery.DeliveryState;
import ru.yandex.practicum.interaction.dto_warehouse.AddressDto;

@UtilityClass
public class DeliveryMapper {
    public DeliveryDto deliveryToDeliveryDto(Delivery delivery, Address toAddress, Address fromAddress) {
        return DeliveryDto.builder()
                .deliveryState(DeliveryState.valueOf(delivery.getDeliveryState().toString()))
                .deliveryId(delivery.getDeliveryId())
                .fromAddress(addressToAddressDto(toAddress))
                .toAddress(addressToAddressDto(fromAddress))
                .orderId(delivery.getOrderId())
                .build();
    }

    public AddressDto addressToAddressDto(Address address) {
        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }

    public Address addressDtoToAddress(AddressDto address) {
        return Address.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }
}
