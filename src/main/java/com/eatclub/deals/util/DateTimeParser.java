package com.eatclub.deals.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class DateTimeParser {
    private static final DateTimeFormatter TIME_FORMATTER_12HR = DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMATTER_24HR = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    /**
     * Helper method to parse a time string, trying multiple formats.
     * This method makes the controller's time parsing as robust as the DataLoader's.
     * @param timeString The time string to parse (e.g., "3:00pm", "15:00").
     * @return LocalTime object.
     * @throws DateTimeParseException if no format matches.
     */
    public LocalTime parseTimeRobustly(String timeString) {
        String upperCaseTimeString = timeString.toUpperCase(Locale.ENGLISH); // Convert to uppercase for robustness
        try {
            return LocalTime.parse(upperCaseTimeString, TIME_FORMATTER_12HR);
        } catch (DateTimeParseException e1) {
            try {
                return LocalTime.parse(upperCaseTimeString, TIME_FORMATTER_24HR);
            } catch (DateTimeParseException e2) {
                throw new DateTimeParseException(
                    "Could not parse time: '" + timeString + "'. Expected formats like '3:00pm', '10:30am' or '15:00', '22:00'.",
                    timeString, 0, e2
                );
            }
        }
    }


}