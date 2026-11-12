package com.algaworks.algashop.ordering.utils;

import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockJwtFactory {

    public static final String DEFAULT_ISSUER_URI = "http://algashop-authorization-server:8081";

    public static final String[] DEFAULT_SCOPES = new String[] {
            "orders:read",
            "orders:write",
            "shopping-carts:read",
            "shopping-carts:write",
            "customers:read",
            "customers:write"
    };

    public static final String DEFAULT_SUBJECT = "6e148bd5-47f6-4022-b9da-07cfaa294f7a";

    public static final String DEFAULT_TOKEN_VALUE = "fake.jwt.token";

    public static final String ADMIN_TOKEN_VALUE = "fake.jwt.admin";

    public static final String UNKNOWN_CUSTOMER_TOKEN_VALUE = "fake.jwt.unknown-customer";

    public static final String NO_SCOPE_TOKEN_VALUE = "fake.jwt.no-scope";

    public static final String EXPIRED_TOKEN_VALUE = "fake.jwt.expired";

    public static final String DEFAULT_ROLE = "CUSTOMER";

    public static final String ADMIN_ROLE = "ADMIN";

    public static final String UNKNOWN_CUSTOMER_SUBJECT = "73677343-9c25-4bff-a1d8-fea3830b6d97";

    public static final String[] DEFAULT_AUDIENCES = {"ecommerce-web-app"};

    public static JwtDecoder createMockJwtDecoder() {
        JwtDecoder jwtDecoder = Mockito.mock(JwtDecoder.class);

        Mockito.when(jwtDecoder.decode(DEFAULT_TOKEN_VALUE))
                .thenReturn(buildDefaultJwt());

        Mockito.when(jwtDecoder.decode(ADMIN_TOKEN_VALUE))
                .thenReturn(buildAdminJwt());

        Mockito.when(jwtDecoder.decode(NO_SCOPE_TOKEN_VALUE))
                .thenReturn(buildNoScopeJwt());

        Mockito.when(jwtDecoder.decode(UNKNOWN_CUSTOMER_TOKEN_VALUE))
                .thenReturn(buildUnknownCustomerJwt());

        Mockito.when(jwtDecoder.decode(EXPIRED_TOKEN_VALUE))
                .thenThrow(new JwtException("Token is expired"));

        return jwtDecoder;
    }

    public static Jwt buildJwt(String tokenValue, String subject,
                               String issuer, String[] scopes, String role, String[] audiences) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(600);

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", subject);
        claims.put("iss", issuer);
        claims.put("role", role);
        claims.put("aud", List.of(audiences));
        claims.put("scope", List.of(scopes));

        return Jwt.withTokenValue(tokenValue)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .issuer(issuer)
                .subject(subject)
                .claims(c -> c.putAll(claims))
                .headers(h -> h.put("alg", "none"))
                .build();
    }

    public static Jwt buildDefaultJwt() {
        return buildJwt(DEFAULT_TOKEN_VALUE, DEFAULT_SUBJECT, DEFAULT_ISSUER_URI, DEFAULT_SCOPES, DEFAULT_ROLE, DEFAULT_AUDIENCES);
    }

    public static Jwt buildNoScopeJwt() {
        return buildJwt(NO_SCOPE_TOKEN_VALUE, DEFAULT_SUBJECT, DEFAULT_ISSUER_URI, new String[]{}, DEFAULT_ROLE, DEFAULT_AUDIENCES);
    }

    public static Jwt buildAdminJwt() {
        return buildJwt(ADMIN_TOKEN_VALUE, DEFAULT_SUBJECT, DEFAULT_ISSUER_URI, DEFAULT_SCOPES, ADMIN_ROLE, DEFAULT_AUDIENCES);
    }

    public static Jwt buildUnknownCustomerJwt() {
        return buildJwt(UNKNOWN_CUSTOMER_TOKEN_VALUE, UNKNOWN_CUSTOMER_SUBJECT, DEFAULT_ISSUER_URI, DEFAULT_SCOPES, DEFAULT_ROLE, DEFAULT_AUDIENCES);
    }

}
