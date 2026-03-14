package com.picketlogia.picket.api.token.model;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccessToken {

    private static final String SECRET = "abcdeffghijklmnopqrstuvwxyz0123456";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final Long EXP = 1000 * 60 * 120L;

    public static final String IDX_NAME = "idx";
    public static final String EMAIL_NAME = "email";
    public static final String ROLE_NAME = "role";
    public static final String USER_TYPE_NAME = "userType";
    public static final String TOKEN_NAME = TokenCookieNames.ACCESS;

    public static String issue(String email, Long idx, String role, String userType) {

        Map<String, String> claims =  new HashMap<>();
        claims.put(IDX_NAME, "" + idx);
        claims.put(EMAIL_NAME, email);
        claims.put(ROLE_NAME, role);
        claims.put(USER_TYPE_NAME, userType);

        return Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getValue(Claims claims, String key) {
        return (String) claims.get(key);
    }

    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String reIssue(String previousAccessToken) {

        Claims claims = getClaims(previousAccessToken);

        return Jwts.builder()
                .setSubject(getValue(claims, EMAIL_NAME))
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}
