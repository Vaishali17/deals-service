package com.eatclub.deals.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eatclub.deals.dto.DealResponseDto;
import com.eatclub.deals.entity.Deal;
import com.eatclub.deals.exception.InvalidInputException;
import com.eatclub.deals.repository.DealRepository;
import com.eatclub.deals.util.DateTimeParser;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class DealController {

    private final DealRepository dealRepository;

    @Autowired
    private DateTimeParser dateTimeParser;

    public DealController(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    /**
     * API endpoint to retrieve a list of active restaurant deals
     * for a specified time of day.
     *
     * @param timeOfDay A string representing the time (e.g., "10:30am", "3:00pm", "15:00").
     * @return A ResponseEntity containing a list of DealResponseDto objects if successful.
     * Error responses for missing or invalid 'timeOfDay' are handled globally
     * by the GlobalExceptionHandler.
     */
    @GetMapping("/deals")
    public ResponseEntity<List<DealResponseDto>> getDealsbyTimeOfDay(@RequestParam String timeOfDay) {
        if (timeOfDay.trim().isEmpty()) {
            if (timeOfDay.trim().isEmpty()) {
                throw new InvalidInputException("The 'timeOfDay' parameter cannot be an empty string.");
            }
        }

        LocalTime queryTime;
        queryTime = dateTimeParser.parseTimeRobustly(timeOfDay);
        List<Deal> activeDeals = dealRepository.findActiveDealsAtTime(queryTime);
        List<DealResponseDto> dealResponseDtos = activeDeals.stream()
                                                            .map(DealResponseDto::fromEntity)
                                                            .collect(Collectors.toList());

        return ResponseEntity.ok(dealResponseDtos);
    }
}