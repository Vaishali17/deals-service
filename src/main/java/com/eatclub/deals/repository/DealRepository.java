package com.eatclub.deals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eatclub.deals.entity.Deal;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {

    /**
     * Finds a Deal by its unique dealObjectId.
     *
     * @param dealObjectId The unique ID of the deal from the external data source.
     * @return An Optional containing the found Deal, or empty if not found.
     */
    Optional<Deal> findByDealObjectId(String dealObjectId);

    /**
     * Finds all active deals for a given time of day.
     * @param queryTime The LocalTime to check for active deals (e.g., 10:30, 15:00).
     * @return A list of active deals.
     */
    @Query("SELECT d FROM Deal d JOIN FETCH d.restaurant " +
           "WHERE d.qtyLeft > 0 " +
           "AND d.isDeleted = FALSE " +
           "AND (:queryTime >= d.startTime AND :queryTime <= d.endTime)")
    List<Deal> findActiveDealsAtTime(LocalTime queryTime);

    /**
     * This method fetches all deals that are not soft-deleted, regardless of their current quantity or specific time.
     *
     * @return A list of all valid (not deleted) deals
     */
    @Query("SELECT d FROM Deal d JOIN FETCH d.restaurant " +
           "WHERE d.isDeleted = FALSE")
    List<Deal> findAllValidDeals();

}

