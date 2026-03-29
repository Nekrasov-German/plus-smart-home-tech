package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWarehouse {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    private Boolean fragile;

    @Embedded
    private Dimension dimension;

    @Column(name = "quantity")
    private Integer quantity;

    private Double weight;
}
