package com.eatclub.deals.service;

import com.eatclub.deals.entity.Deal;
import com.eatclub.deals.entity.Restaurant;
import com.eatclub.deals.repository.DealRepository;
import com.eatclub.deals.service.PeakTimeCalculatorService.PeakTimeWindow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeakTimeCalculatorServiceTest {

    @Mock
    private DealRepository dealRepository;

    @InjectMocks
    private PeakTimeCalculatorService peakTimeCalculatorService;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(99L);
        restaurant.setRestaurantObjectId("dummy-restaurant-id");
        restaurant.setRestaurantName("Dummy Restaurant");
        restaurant.setOpenTime(LocalTime.of(8, 0));
        restaurant.setCloseTime(LocalTime.of(22, 0));
        restaurant.setCreatedAt(Instant.now());
        restaurant.setUpdatedAt(Instant.now());
        restaurant.setIsDeleted(false);
    }

    private Deal createDeal(Long id, LocalTime startTime, LocalTime endTime) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setDealObjectId("deal-obj-" + id);
        deal.setRestaurant(restaurant); // Link to the dummy restaurant
        deal.setStartTime(startTime);
        deal.setEndTime(endTime);
        deal.setQtyLeft(1);
        deal.setIsDeleted(false);
        deal.setRestaurantNameDenormalized("Dummy Restaurant Name");
        deal.setDealDescription("Test Deal");
        deal.setDiscount(10.0);
        deal.setDineIn(true);
        deal.setLightning(false);
        deal.setCreatedAt(Instant.now());
        deal.setUpdatedAt(Instant.now());
        return deal;
    }

    /**
     * Test case: No deals available.
     * Expected: PeakTimeWindow with null start and end times (no content).
     */
    @Test
    void calculatePeakTimeWindow_NoDeals() {
        when(dealRepository.findAllValidDeals()).thenReturn(Collections.emptyList());

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertNull(result.getPeakTimeStart(), "Peak start time should be null for no deals");
        assertNull(result.getPeakTimeEnd(), "Peak end time should be null for no deals");
    }

    /**
     * Test case: Deals exist but none overlap to create a peak of more than 1.
     * This tests the scenario where each deal individually creates a "peak" of 1.
     * The algorithm should return the first such peak.
     * Expected: PeakTimeWindow with the start and end times of the first deal's interval.
     */
    @Test
    void calculatePeakTimeWindow_DealsExistWithIndividualPeaks() {
        List<Deal> deals = Arrays.asList(
                createDeal(1L, LocalTime.of(9, 0), LocalTime.of(9, 29)), 
                createDeal(2L, LocalTime.of(10, 0), LocalTime.of(10, 29))
        );
        when(dealRepository.findAllValidDeals()).thenReturn(deals);

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertEquals(LocalTime.of(9, 0), result.getPeakTimeStart(), "Peak start time should be 09:00 for the first individual peak");
        assertEquals(LocalTime.of(9, 30), result.getPeakTimeEnd(), "Peak end time should be 09:30 for the first individual peak");
    }

    /**
     * Test case: A clear peak exists in a single 30-minute interval.
     * Example: Most deals active between 10:00 and 10:30.
     */
    @Test
    void calculatePeakTimeWindow_SinglePeakInterval() {
        List<Deal> deals = Arrays.asList(
                createDeal(1L, LocalTime.of(9, 0), LocalTime.of(10, 15)), 
                createDeal(2L, LocalTime.of(9, 45), LocalTime.of(10, 45)),
                createDeal(3L, LocalTime.of(10, 0), LocalTime.of(10, 30))
        );
        when(dealRepository.findAllValidDeals()).thenReturn(deals);

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertEquals(LocalTime.of(10, 0), result.getPeakTimeStart(), "Peak start time should be 10:00");
        assertEquals(LocalTime.of(10, 30), result.getPeakTimeEnd(), "Peak end time should be 10:30");
    }

    /**
     * Test case: A peak extends over multiple contiguous 30-minute intervals.
     * Example: Peak from 12:00 to 13:30.
     */
    @Test
    void calculatePeakTimeWindow_ContiguousPeakWindow() {
        List<Deal> deals = Arrays.asList(
                createDeal(1L, LocalTime.of(11, 30), LocalTime.of(13, 30)),
                createDeal(2L, LocalTime.of(11, 45), LocalTime.of(13, 0)), 
                createDeal(3L, LocalTime.of(12, 0), LocalTime.of(13, 15)),
                createDeal(4L, LocalTime.of(12, 15), LocalTime.of(12, 45))
        );
        when(dealRepository.findAllValidDeals()).thenReturn(deals);

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertEquals(LocalTime.of(12, 0), result.getPeakTimeStart(), "Peak start time should be 12:00");
        assertEquals(LocalTime.of(13, 0), result.getPeakTimeEnd(), "Peak end time should be 13:00 (12:00 + 2*30 min intervals)");
    }

    /**
     * Test case: Deals spanning midnight.
     * Example: A deal from 23:00 to 01:00 should correctly increment counts across midnight.
     */
    @Test
    void calculatePeakTimeWindow_DealsSpanningMidnight() {
        List<Deal> deals = Arrays.asList(
                createDeal(1L, LocalTime.of(23, 0), LocalTime.of(1, 0)),
                createDeal(2L, LocalTime.of(23, 30), LocalTime.of(0, 30)),
                createDeal(3L, LocalTime.of(0, 0), LocalTime.of(0, 45))
        );
        when(dealRepository.findAllValidDeals()).thenReturn(deals);

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertEquals(LocalTime.of(0, 0), result.getPeakTimeStart(), "Peak start time should be 00:00");
        assertEquals(LocalTime.of(0, 30), result.getPeakTimeEnd(), "Peak end time should be 00:30");
    }

    /**
     * Test case: Deals ending exactly at midnight (00:00).
     * Expected: The last interval of the day (e.g., 23:30-00:00) should be covered.
     */
    @Test
    void calculatePeakTimeWindow_DealEndsAtMidnight() {
        List<Deal> deals = Arrays.asList(
                createDeal(1L, LocalTime.of(23, 0), LocalTime.MIDNIGHT)
        );
        when(dealRepository.findAllValidDeals()).thenReturn(deals);

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertEquals(LocalTime.of(23, 0), result.getPeakTimeStart(), "Peak start time should be 23:00");
        assertEquals(LocalTime.MIDNIGHT, result.getPeakTimeEnd(), "Peak end time should be 00:00");
    }

    /**
     * Test case: Multiple non-contiguous peak windows with the same maximum deal count.
     * Expected: The earliest *longest* peak window should be returned.
     * Example: Max deals 3 at 10:00-10:30 and 14:00-14:30. Picks 10:00-10:30.
     * Example: Max deals 3 at 10:00-11:00 (2 intervals) and 14:00-14:30 (1 interval). Picks 10:00-11:00.
     */
    @Test
    void calculatePeakTimeWindow_MultipleNonContiguousPeaks() {
        List<Deal> deals = Arrays.asList(
                createDeal(1L, LocalTime.of(9, 45), LocalTime.of(11, 15)), 
                createDeal(2L, LocalTime.of(10, 0), LocalTime.of(10, 45)),
                createDeal(3L, LocalTime.of(10, 15), LocalTime.of(11, 0)),
                
                createDeal(4L, LocalTime.of(13, 45), LocalTime.of(15, 15)),
                createDeal(5L, LocalTime.of(14, 0), LocalTime.of(14, 45)),
                createDeal(6L, LocalTime.of(14, 15), LocalTime.of(15, 0))
        );
        when(dealRepository.findAllValidDeals()).thenReturn(deals);

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertEquals(LocalTime.of(10, 0), result.getPeakTimeStart(), "Peak start time should be the earliest longest peak at 10:00");
        assertEquals(LocalTime.of(11, 0), result.getPeakTimeEnd(), "Peak end time should be 11:00");
    }

    /**
     * Test case: All intervals have the same number of deals.
     * Expected: The entire day (00:00-00:00) should be returned as the peak,
     * as it represents the longest contiguous run at the max count.
     */
    @Test
    void calculatePeakTimeWindow_AllIntervalsSameDeals() {
        List<Deal> deals = Arrays.asList(
                createDeal(1L, LocalTime.MIDNIGHT, LocalTime.MIDNIGHT)
        );
        when(dealRepository.findAllValidDeals()).thenReturn(deals);

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertEquals(LocalTime.MIDNIGHT, result.getPeakTimeStart(), "Peak start time should be 00:00");
        assertEquals(LocalTime.MIDNIGHT, result.getPeakTimeEnd(), "Peak end time should be 00:00 (end of day)");
    }

    /**
     * Test case: Deals active only for a specific short period, less than one interval.
     * Expected: The interval containing the deal's start time should be the peak.
     * This test highlights how INTERVAL_GRANULARITY_MINUTES impacts precision.
     */
    @Test
    void calculatePeakTimeWindow_ShortDurationDeal() {
        List<Deal> deals = Arrays.asList(
                createDeal(1L, LocalTime.of(14, 10), LocalTime.of(14, 20))
        );
        when(dealRepository.findAllValidDeals()).thenReturn(deals);

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertEquals(LocalTime.of(14, 0), result.getPeakTimeStart(), "Peak start time should be 14:00 (start of its interval)");
        assertEquals(LocalTime.of(14, 30), result.getPeakTimeEnd(), "Peak end time should be 14:30 (end of its interval)");
    }

    /**
     * Test case: A deal active across two specific intervals, confirming boundary handling.
     */
    @Test
    void calculatePeakTimeWindow_DealAcrossIntervalBoundary() {
        List<Deal> deals = Arrays.asList(
                createDeal(1L, LocalTime.of(13, 45), LocalTime.of(14, 15))
        );
        when(dealRepository.findAllValidDeals()).thenReturn(deals);

        PeakTimeWindow result = peakTimeCalculatorService.calculatePeakTimeWindow();

        assertNotNull(result);
        assertEquals(LocalTime.of(13, 30), result.getPeakTimeStart(), "Peak start time should be 13:30");
        assertEquals(LocalTime.of(14, 30), result.getPeakTimeEnd(), "Peak end time should be 14:30");
    }
}
