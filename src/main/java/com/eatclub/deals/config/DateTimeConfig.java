package com.eatclub.deals.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class DateTimeConfig {

    @Bean("hhmmFormatter")
    public DateTimeFormatter hhmmFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm");
    }

    @Bean("hhmmaFormatter")
    public DateTimeFormatter hhmmaFormatter() {
        return DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);
    }
}
