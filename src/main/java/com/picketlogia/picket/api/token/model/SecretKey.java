package com.picketlogia.picket.api.token.model;

import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class SecretKey {

    private static final String SECRET = "abcdeffghijklmnopqrstuvwxyz0123456";

    public static Key generateKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}
