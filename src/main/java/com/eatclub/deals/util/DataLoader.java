package com.eatclub.deals.util;

import com.eatclub.deals.entity.Deal;
import com.eatclub.deals.entity.Restaurant;
import com.eatclub.deals.repository.DealRepository;
import com.eatclub.deals.repository.RestaurantRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Component
@Profile("local")
public class DataLoader implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final DealRepository dealRepository;
    private final ObjectMapper objectMapper;

    @Value("classpath:challengedata.json")
    private Resource jsonData;

    public DataLoader(RestaurantRepository restaurantRepository, DealRepository dealRepository) {
        this.restaurantRepository = restaurantRepository;
        this.dealRepository = dealRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("DataLoader: Starting to load data from challengedata.json...");

        try (InputStream is = jsonData.getInputStream()) {
            RestaurantDataWrapper data = objectMapper.readValue(is, RestaurantDataWrapper.class);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

            for (RestaurantJson restaurantJson : data.getRestaurants()) {
                Restaurant restaurant = new Restaurant();
                restaurant.setRestaurantObjectId(restaurantJson.getObjectId());
                restaurant.setRestaurantName(restaurantJson.getName());
                restaurant.setRestaurantAddress1(restaurantJson.getAddress1());
                restaurant.setRestarantSuburb(restaurantJson.getSuburb());

                if (restaurantJson.getCuisines() != null && !restaurantJson.getCuisines().isEmpty()) {
                    restaurant.setCuisines(restaurantJson.getCuisines().stream()
                                                            .map(String::trim)
                                                            .collect(Collectors.joining(", ")));
                } else {
                    restaurant.setCuisines("");
                }
                restaurant.setImageLink(restaurantJson.getImageLink());

                restaurant.setOpenTime(LocalTime.parse(restaurantJson.getOpen().toUpperCase(Locale.ENGLISH), timeFormatter));
                restaurant.setCloseTime(LocalTime.parse(restaurantJson.getClose().toUpperCase(Locale.ENGLISH), timeFormatter));


                restaurant = restaurantRepository.save(restaurant);

                for (DealJson dealJson : restaurantJson.getDeals()) {
                    Deal deal = new Deal();
                    deal.setDealObjectId(dealJson.getObjectId());
                    deal.setRestaurant(restaurant);
                    deal.setRestaurantNameDenormalized(restaurant.getRestaurantName());
                    deal.setDealDescription(null);

                    deal.setDiscount(Double.parseDouble(dealJson.getDiscount()));
                    deal.setDineIn(Boolean.parseBoolean(dealJson.getDineIn()));
                    deal.setLightning(Boolean.parseBoolean(dealJson.getLightning()));
                    deal.setQtyLeft(Integer.parseInt(dealJson.getQtyLeft()));


                    String dealStartTimeStr = dealJson.getStart() != null ? dealJson.getStart() : restaurantJson.getOpen();
                    String dealEndTimeStr = dealJson.getEnd() != null ? dealJson.getEnd() : restaurantJson.getClose();

                    deal.setStartTime(LocalTime.parse(dealStartTimeStr.toUpperCase(Locale.ENGLISH), timeFormatter));
                    deal.setEndTime(LocalTime.parse(dealEndTimeStr.toUpperCase(Locale.ENGLISH), timeFormatter));

                    dealRepository.save(deal);
                }
            }
            System.out.println("DataLoader: Successfully loaded data into the database.");

        } catch (Exception e) {
            System.err.println("DataLoader: Error loading data: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Data
    private static class RestaurantDataWrapper {
        @JsonProperty("restaurants")
        private List<RestaurantJson> restaurants;
    }

    @Data
    private static class RestaurantJson {
        private String objectId;
        private String name;
        private String address1;
        private String suburb;
        private List<String> cuisines;
        private String imageLink;
        private String open;
        private String close;
        private List<DealJson> deals;
    }

    @Data
    private static class DealJson {
        private String objectId;
        private String discount;
        private String dineIn;
        private String lightning;
        private String qtyLeft;
        private String open;
        private String close;
        private String start;
        private String end;
    }
}
