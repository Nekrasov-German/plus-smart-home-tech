package ru.yandex.practicum.delivery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.delivery.dal.AddressRepository;

import java.util.UUID;

@Entity
@Table(name = "delivery")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deliveryId;

    @Column(name = "from_address_id", nullable = false)
    private UUID fromAddressId;

    @Column(name = "to_address_id", nullable = false)
    private UUID toAddressId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state", nullable = false, length = 20)
    private State deliveryState = State.CREATED;

    public Address getFromAddress(AddressRepository addressRepository) {
        return addressRepository.findById(fromAddressId).orElse(null);
    }

    public Address getToAddress(AddressRepository addressRepository) {
        return addressRepository.findById(toAddressId).orElse(null);
    }
}
