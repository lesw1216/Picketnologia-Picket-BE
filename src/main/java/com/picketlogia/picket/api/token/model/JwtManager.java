package com.picketlogia.picket.api.token.model;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtManager {

    public static Claims parseJWT(String jwt) {

        return Jwts.parserBuilder()
                .setSigningKey(SecretKey.generateKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
