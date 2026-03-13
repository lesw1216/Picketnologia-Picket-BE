package com.picketlogia.picket.api.token.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
public class RefreshToken {

    @Getter
    private final String value;
    private final Integer expireMillis;

    private RefreshToken() {
        this.value = UUID.randomUUID().toString();
        this.expireMillis = 1000 * 60 * 60 * 24 * 7;
    }

    public static RefreshToken issue() {
        return new RefreshToken();
    }

    public Integer getExpireSecond() {
        return this.expireMillis / 1000;
    }

    public Long getExpireMillis() {
        return this.expireMillis.longValue();
    }
}
