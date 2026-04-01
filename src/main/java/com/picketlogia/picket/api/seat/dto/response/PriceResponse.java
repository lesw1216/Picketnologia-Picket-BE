package com.picketlogia.picket.api.seat.dto.response;

import com.picketlogia.picket.api.seat.dto.result.PriceResult;
import lombok.Builder;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
@Builder
public class PriceResponse {

    private Long price;
    private String priceFormat;

    public static PriceResponse from(PriceResult result) {
        return PriceResponse.builder()
                .price(result.getPrice())
                .priceFormat(result.getPriceFormat())
                .build();
    }

    public static PriceResponse from(Long price) {
        return PriceResponse.builder()
                .price(price)
                .priceFormat(formatToMoney(price))
                .build();
    }

    private static String formatToMoney(Long price) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(price) + "원";
    }
}
