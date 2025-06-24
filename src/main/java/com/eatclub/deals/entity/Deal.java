package com.eatclub.deals.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "deals", indexes = {
    @Index(name = "idx_deal_object_id", columnList = "deal_object_id", unique = true),
    @Index(name = "idx_restaurant_id", columnList = "restaurant_id"),
    @Index(name = "idx_deal_time_range", columnList = "start_time, end_time"),
    @Index(name = "idx_deal_qty_left", columnList = "qty_left")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deal_object_id", unique = true, nullable = false, length = 50)
    private String dealObjectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "restaurant_name_denormalized", nullable = false, length = 255)
    private String restaurantNameDenormalized;

    @Column(name = "description", length = 500)
    private String dealDescription;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "dine_in", nullable = false)
    private Boolean dineIn;

    @Column(name = "lightning", nullable = false)
    private Boolean lightning;

    @Column(name = "qty_left", nullable = false)
    private Integer qtyLeft;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
