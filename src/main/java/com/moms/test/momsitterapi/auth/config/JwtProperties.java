package com.moms.test.momsitterapi.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.yml")
public class JwtProperties {
    private static final long ONE_MINUTE = 60*1000;
    private static final long ONE_HOURS = 60*ONE_MINUTE;
    private static final long ONE_DAY = 24*ONE_HOURS;


    public static String SECRET;
    @Value("${jwt.secret}")
    public void setSECRET(String SECRET){JwtProperties.SECRET = SECRET;}
    public static long ACCESS_TOKEN_EXPIRATION_TIME = ONE_HOURS*2;
    @Value("${jwt.access-token-expireTime}")
    public void setACCESS_TOKEN_EXPIRATION_TIME(long value){JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME = value;}
    public static long REFRESH_TOKEN_EXPIRATION_TIME = ONE_DAY * 30;
    @Value("${jwt.refresh-token-expireTime}")
    public void setREFRESH_TOKEN_EXPIRATION_TIME(long value){JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME = value;}
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_AUTH = "auth";

    public static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
}
