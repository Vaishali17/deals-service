package com.eatclub.deals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<com.eatclub.deals.entity.Restaurant, Long> {

    /**
     * Finds a Restaurant by its unique restaurantObjectId.
     * This method is automatically implemented by Spring Data JPA based on the method name.
     *
     * @param restaurantObjectId The unique ID of the restaurant from the external data source.
     * @return An Optional containing the found Restaurant, or empty if not found.
     */
    Optional<com.eatclub.deals.entity.Restaurant> findByRestaurantObjectId(String restaurantObjectId);

}
