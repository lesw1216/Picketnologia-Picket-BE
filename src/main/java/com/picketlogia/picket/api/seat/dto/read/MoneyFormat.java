package com.picketlogia.picket.api.seat.dto.read;

import lombok.Builder;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
@Builder
public class MoneyFormat {

    private Long price;
    private String priceFormat;

    public static MoneyFormat from(Long price) {
        return MoneyFormat.builder()
                .price(price)
                .priceFormat(formatToMoney(price))
                .build();
    }

    private static String formatToMoney(Long price) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(price) + "원";
    }
}
