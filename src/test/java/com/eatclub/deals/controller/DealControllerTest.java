package com.eatclub.deals.controller;

import com.eatclub.deals.entity.Deal;
import com.eatclub.deals.entity.Restaurant;
import com.eatclub.deals.exception.GlobalExceptionHandler;
import com.eatclub.deals.repository.DealRepository;
import com.eatclub.deals.service.PeakTimeCalculatorService;
import com.eatclub.deals.util.DateTimeParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealController.class)
@Import(GlobalExceptionHandler.class)
public class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealRepository dealRepository;

    @MockBean
    private PeakTimeCalculatorService peakTimeCalculatorService;

    @MockBean
    private DateTimeParser dateTimeParser;

    private static final DateTimeFormatter PEAK_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private Restaurant createSampleRestaurant(Long id, String restaurantObjectId, String name,
                                              String address1, String suburb, LocalTime openTime, LocalTime closeTime) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setRestaurantObjectId(restaurantObjectId);
        restaurant.setRestaurantName(name);
        restaurant.setRestaurantAddress1(address1);
        restaurant.setRestarantSuburb(suburb);
        restaurant.setOpenTime(openTime);
        restaurant.setCloseTime(closeTime);
        restaurant.setCuisines("Various");
        restaurant.setImageLink("http://example.com/image.jpg");
        restaurant.setCreatedAt(Instant.now());
        restaurant.setUpdatedAt(Instant.now());
        restaurant.setIsDeleted(false);
        return restaurant;
    }


    private Deal createSampleDeal(Long id, String dealObjectId, Restaurant restaurant,
                                  String dealDescription, Double discount, Boolean dineIn,
                                  Boolean lightning, Integer qtyLeft, LocalTime startTime, LocalTime endTime) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setDealObjectId(dealObjectId);
        deal.setRestaurant(restaurant);
        deal.setRestaurantNameDenormalized(restaurant.getRestaurantName());
        deal.setDealDescription(dealDescription);
        deal.setDiscount(discount);
        deal.setDineIn(dineIn);
        deal.setLightning(lightning);
        deal.setQtyLeft(qtyLeft);
        deal.setStartTime(startTime);
        deal.setEndTime(endTime);
        deal.setCreatedAt(Instant.now());
        deal.setUpdatedAt(Instant.now());
        deal.setIsDeleted(false);
        return deal;
    }


    /**
     * Test case for a successful request with valid time.
     * Expected: HTTP 200 OK and a list of DealResponseDto with all fields validated.
     * Uses a reduced set of dummy data for better readability.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getDealsByTimeOfDay_Success() throws Exception {
        String timeOfDayParam = "10:00am";
        LocalTime parsedTime = LocalTime.of(10, 0);
        when(dateTimeParser.parseTimeRobustly(timeOfDayParam)).thenReturn(parsedTime);

        Restaurant abcChicken = createSampleRestaurant(
                101L, "D80263E8-FD89-2C70-FF6B-D854ADB8DB00", "ABC Chicken",
                "361 Queen Street", "Melbourne", LocalTime.of(12, 0), LocalTime.of(23, 0)
        );
        Restaurant kekou = createSampleRestaurant(
                102L, "B5713CD0-91BF-40C7-AFC3-7D46D26B00BF", "Kekou",
                "396 Bridge Road", "Richmond", LocalTime.of(13, 0), LocalTime.of(23, 0)
        );

        Deal deal1 = createSampleDeal(
                1L, "D80263E8-0000-2C70-FF6B-D854ADB8DB00", abcChicken, "Chicken Combo Deal",
                30.0, false, false, 1, LocalTime.of(12,0), LocalTime.of(23,0)
        );
        Deal deal2 = createSampleDeal(
                2L, "B5713CD0-0000-40C7-AFC3-7D46D26B00BF", kekou, "Noodle Bowl Special",
                10.0, true, true, 3, LocalTime.of(13,0), LocalTime.of(23,0)
        );

        when(dealRepository.findActiveDealsAtTime(parsedTime)).thenReturn(Arrays.asList(
            deal1, deal2
        ));


        mockMvc.perform(get("/v1/deals")
                        .param("timeOfDay", timeOfDayParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].restaurantObjectId").value("D80263E8-FD89-2C70-FF6B-D854ADB8DB00"))
                .andExpect(jsonPath("$[0].restaurantName").value("ABC Chicken"))
                .andExpect(jsonPath("$[0].restaurantAddress1").value("361 Queen Street"))
                .andExpect(jsonPath("$[0].restarantSuburb").value("Melbourne"))
                .andExpect(jsonPath("$[0].restaurantOpen").value("12:00PM"))
                .andExpect(jsonPath("$[0].restaurantClose").value("11:00PM"))
                .andExpect(jsonPath("$[0].dealObjectId").value("D80263E8-0000-2C70-FF6B-D854ADB8DB00"))
                .andExpect(jsonPath("$[0].discount").value(30.0))
                .andExpect(jsonPath("$[0].dineIn").value(false))
                .andExpect(jsonPath("$[0].lightning").value(false))
                .andExpect(jsonPath("$[0].qtyLeft").value(1))

                .andExpect(jsonPath("$[1].restaurantObjectId").value("B5713CD0-91BF-40C7-AFC3-7D46D26B00BF"))
                .andExpect(jsonPath("$[1].restaurantName").value("Kekou"))
                .andExpect(jsonPath("$[1].restaurantAddress1").value("396 Bridge Road"))
                .andExpect(jsonPath("$[1].restarantSuburb").value("Richmond"))
                .andExpect(jsonPath("$[1].restaurantOpen").value("1:00PM"))
                .andExpect(jsonPath("$[1].restaurantClose").value("11:00PM"))
                .andExpect(jsonPath("$[1].dealObjectId").value("B5713CD0-0000-40C7-AFC3-7D46D26B00BF"))
                .andExpect(jsonPath("$[1].discount").value(10.0))
                .andExpect(jsonPath("$[1].dineIn").value(true))
                .andExpect(jsonPath("$[1].lightning").value(true))
                .andExpect(jsonPath("$[1].qtyLeft").value(3));
    }


    /**
     * Test case for when the 'timeOfDay' parameter is missing.
     * This scenario is handled by Spring's default behavior and
     * then caught by GlobalExceptionHandler (MissingServletRequestParameterException).
     * Expected: HTTP 400 Bad Request with a specific error code and message.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getDealsByTimeOfDay_MissingTimeOfDayParameter() throws Exception {
        mockMvc.perform(get("/v1/deals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MISSING_PARAMETER"))
                .andExpect(jsonPath("$.errorMessage").value("The 'timeOfDay' parameter is required and cannot be empty."));
    }


    /**
     * Test case for when the 'timeOfDay' parameter is an empty string.
     * This scenario is explicitly handled by the controller's `if (timeOfDay.trim().isEmpty())` check,
     * which throws an InvalidInputException, caught by GlobalExceptionHandler.
     * Expected: HTTP 400 Bad Request with a specific error code and message.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getDealsByTimeOfDay_EmptyTimeOfDayParameter() throws Exception {
        String emptyTimeOfDayParam = "";

        mockMvc.perform(get("/v1/deals")
                        .param("timeOfDay", emptyTimeOfDayParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.errorMessage").value("The 'timeOfDay' parameter cannot be an empty string."));
    }


    /**
     * Test case for when the 'timeOfDay' parameter is only whitespace.
     * This also triggers the `if (timeOfDay.trim().isEmpty())` check.
     * Expected: HTTP 400 Bad Request with a specific error code and message.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getDealsByTimeOfDay_WhitespaceTimeOfDayParameter() throws Exception {
        String whitespaceTimeOfDayParam = "   ";

        mockMvc.perform(get("/v1/deals")
                        .param("timeOfDay", whitespaceTimeOfDayParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.errorMessage").value("The 'timeOfDay' parameter cannot be an empty string."));
    }


    /**
     * Test case for when the 'timeOfDay' parameter has an invalid format.
     * This scenario causes DateTimeParser to throw DateTimeParseException,
     * which is caught by GlobalExceptionHandler.
     * Expected: HTTP 400 Bad Request with a specific error code and message.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getDealsByTimeOfDay_InvalidTimeFormat() throws Exception {
        String invalidTimeFormatParam = "not-a-time";

        when(dateTimeParser.parseTimeRobustly(anyString()))
                .thenThrow(new DateTimeParseException("Text 'not-a-time' could not be parsed", invalidTimeFormatParam, 0));

        mockMvc.perform(get("/v1/deals")
                        .param("timeOfDay", invalidTimeFormatParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_TIME_FORMAT"))
                .andExpect(jsonPath("$.errorMessage").value("Invalid time format provided. Please ensure it's a valid time string. Text 'not-a-time' could not be parsed"));
    }


    /**
     * Test case for an unexpected internal server error.
     * This simulates a generic Exception being thrown somewhere in the logic
     * (e.g., repository or parser throwing an unexpected runtime exception)
     * which is caught by the generic handler in GlobalExceptionHandler.
     * Expected: HTTP 500 Internal Server Error with a generic message.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getDealsByTimeOfDay_InternalServerError() throws Exception {
        String timeOfDayParam = "10:00am";
        LocalTime parsedTime = LocalTime.of(10, 0);

        when(dateTimeParser.parseTimeRobustly(timeOfDayParam)).thenReturn(parsedTime);

        when(dealRepository.findActiveDealsAtTime(parsedTime))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/v1/deals")
                        .param("timeOfDay", timeOfDayParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("An unexpected error occurred. Please try again later."));
    }


    /**
     * Test case for when no deals are found.
     * Expected: HTTP 200 OK and an empty list.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getDealsByTimeOfDay_NoDealsFound() throws Exception {
        String timeOfDayParam = "12:00pm";
        LocalTime parsedTime = LocalTime.of(12, 0);

        when(dateTimeParser.parseTimeRobustly(timeOfDayParam)).thenReturn(parsedTime);

        when(dealRepository.findActiveDealsAtTime(parsedTime)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/deals")
                        .param("timeOfDay", timeOfDayParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)); // Expect an empty array
    }

    /**
     * Test case for the /peak-time endpoint when a peak time window is successfully calculated.
     * Expected: HTTP 200 OK and a PeakTimeResponse with start and end times.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getPeakDealTime_Success() throws Exception {
        LocalTime peakStart = LocalTime.of(12, 0);
        LocalTime peakEnd = LocalTime.of(13, 0);
        PeakTimeCalculatorService.PeakTimeWindow peakWindow =
                new PeakTimeCalculatorService.PeakTimeWindow(peakStart, peakEnd);

        when(peakTimeCalculatorService.calculatePeakTimeWindow()).thenReturn(peakWindow);

        mockMvc.perform(get("/v1/peak-time")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peakTimeStart").value(PEAK_TIME_FORMATTER.format(peakStart)))
                .andExpect(jsonPath("$.peakTimeEnd").value(PEAK_TIME_FORMATTER.format(peakEnd)));
    }

    /**
     * Test case for the /peak-time endpoint when no peak time window can be determined.
     * This occurs if the service returns a PeakTimeWindow with null start time.
     * Expected: HTTP 204 No Content.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getPeakDealTime_NoContent() throws Exception {
        PeakTimeCalculatorService.PeakTimeWindow peakWindow =
                new PeakTimeCalculatorService.PeakTimeWindow(null, null);

        when(peakTimeCalculatorService.calculatePeakTimeWindow()).thenReturn(peakWindow);

        mockMvc.perform(get("/v1/peak-time")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

     /**
     * Test case for the /peak-time endpoint when an internal server error occurs in the service.
     * This simulates an unexpected RuntimeException from PeakTimeCalculatorService.
     * Expected: HTTP 500 Internal Server Error with a generic error message.
     *
     * @throws Exception If an error occurs during the mock MVC request.
     */
    @Test
    void getPeakDealTime_InternalServerError() throws Exception {
        when(peakTimeCalculatorService.calculatePeakTimeWindow())
                .thenThrow(new RuntimeException("Simulated internal error during peak time calculation"));

        mockMvc.perform(get("/v1/peak-time")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("An unexpected error occurred. Please try again later."));
    }
}