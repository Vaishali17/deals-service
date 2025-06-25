package com.eatclub.deals.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.eatclub.deals.entity.Deal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealResponseDto {

    // Restaurant-related fields (flattened as per the image)
    private String restaurantObjectId;
    private String restaurantName;
    private String restaurantAddress1;
    private String restarantSuburb;
    private String restaurantOpen;
    private String restaurantClose;

    // Deal-related fields
    private String dealObjectId;
    private Double discount;
    private Boolean dineIn;
    private Boolean lightning;
    private Integer qtyLeft;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

    /**
     * Static factory method to create a DealResponseDto from a Deal entity.
     * This now populates both deal-specific and flattened restaurant fields.
     *
     * @param deal The Deal entity to convert.
     * @return A populated DealResponseDto.
     */
    public static DealResponseDto fromEntity(Deal deal) {
        DealResponseDto dto = new DealResponseDto();

        if (deal.getRestaurant() != null) {
            dto.setRestaurantObjectId(deal.getRestaurant().getRestaurantObjectId());
            dto.setRestaurantName(deal.getRestaurantNameDenormalized());
            dto.setRestaurantAddress1(deal.getRestaurant().getRestaurantAddress1());
            dto.setRestarantSuburb(deal.getRestaurant().getRestarantSuburb());
            dto.setRestaurantOpen(deal.getRestaurant().getOpenTime().format(TIME_FORMATTER).toUpperCase(Locale.ENGLISH));
            dto.setRestaurantClose(deal.getRestaurant().getCloseTime().format(TIME_FORMATTER).toUpperCase(Locale.ENGLISH));
        }

        // Populate Deal details
        dto.setDealObjectId(deal.getDealObjectId());
        dto.setDiscount(deal.getDiscount());
        dto.setDineIn(deal.getDineIn());
        dto.setLightning(deal.getLightning());
        dto.setQtyLeft(deal.getQtyLeft());

        return dto;
    }
}