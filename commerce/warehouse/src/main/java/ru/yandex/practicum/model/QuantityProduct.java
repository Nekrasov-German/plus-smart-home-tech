package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "quantity")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuantityProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity")
    private Integer quantity;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId  // Использует ID от ProductWarehouse
    @JoinColumn(name = "product_id")
    private ProductWarehouse product;
}
