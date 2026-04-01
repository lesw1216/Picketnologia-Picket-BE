package com.picketlogia.picket.api.seat.dto.result;

import lombok.Builder;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
@Builder
public class PriceResult {

    private Long price;
    private String priceFormat;

    public static PriceResult from(Long price) {
        return PriceResult.builder()
                .price(price)
                .priceFormat(formatToMoney(price))
                .build();
    }

    private static String formatToMoney(Long price) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(price) + "원";
    }
}
