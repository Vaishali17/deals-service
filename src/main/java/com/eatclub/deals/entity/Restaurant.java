package com.eatclub.deals.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "restaurants", indexes = {
    @Index(name = "idx_restaurant_object_id", columnList = "restaurant_object_id", unique = true),
    @Index(name = "idx_restaurant_name", columnList = "name"),
    @Index(name = "idx_restaurant_suburb", columnList = "suburb")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_object_id", unique = true, nullable = false, length = 50)
    private String restaurantObjectId;

    @Column(name = "name", nullable = false, length = 255)
    private String restaurantName;

    @Column(name = "address_1", length = 255)
    private String restaurantAddress1;

    @Column(name = "suburb", length = 100)
    private String restarantSuburb;

    @Column(name = "cuisines", length = 500)
    private String cuisines;

    @Column(name = "image_link", length = 1024)
    private String imageLink;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

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