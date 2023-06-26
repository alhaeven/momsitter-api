package com.moms.test.momsitterapi.auth;

import com.moms.test.momsitterapi.auth.config.JwtProperties;
import com.moms.test.momsitterapi.auth.dto.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private Key key = null;

    private Key getKey() {
        // initialize
        if (key == null) {
            byte[] keyBytes = Decoders.BASE64.decode(JwtProperties.SECRET);
            key = Keys.hmacShaKeyFor(keyBytes);
        }
        return key;
    }

    // generate Access Token, RefreshToken by user info
    public TokenInfo generateToken(Authentication authentication) {
        Date nowDate = new Date();
        long now = nowDate.getTime();
        Date accessExpireDate = new Date(now + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME);
        Date refreshExpireDate = new Date(now + JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME);

        // Access Token
        String accessToken = generateAccessToken(nowDate, accessExpireDate, authentication.getName(), authentication.getAuthorities());

        // Refresh Token
        // Todo: refresh token 추가 구현 필요
//        String refreshToken = generateRefreshToken(refreshExpireDate);

        return TokenInfo.builder()
                        .accessToken(accessToken)
//                        .refreshToken(refreshToken)
                        .build();
    }

    public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Date nowDate = new Date();
        long now = nowDate.getTime();
        Date accessExpireDate = new Date(now + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME);

        return generateAccessToken(nowDate, accessExpireDate, username, authorities);
    }

    private String generateAccessToken(Date nowDate, Date expireDate, String username, Collection<? extends GrantedAuthority> authorities) {

        String authority = authorities.stream()
                                      .map(GrantedAuthority::getAuthority)
                                      .collect(Collectors.joining(","));

        String accessToken = Jwts.builder()
                                 .setSubject(username)
                                 .claim(JwtProperties.TOKEN_AUTH, authority)
                                 .setIssuedAt(nowDate)
                                 .setExpiration(expireDate)
                                 .signWith(getKey(), SignatureAlgorithm.HS256)
                                 .compact();

        return accessToken;
    }

    private String generateRefreshToken(Date expireDate) {
        String refreshToken = Jwts.builder()
                                  .setExpiration(expireDate)
                                  .signWith(getKey(), SignatureAlgorithm.HS256)
                                  .compact();
        return refreshToken;
    }

    public Authentication getAuthentication(String accessToken) {
        // decode Token
        Claims claims = parseClaims(accessToken);

        if (claims.getSubject() == null) {
            throw new RuntimeException("Not exists subject in token");
        }

        if (claims.get(JwtProperties.TOKEN_AUTH) == null) {
            throw new RuntimeException("Not exists authority in token");
        }

        // get authority in claim info
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(JwtProperties.TOKEN_AUTH)
                                                                                 .toString()
                                                                                 .split(","))
                                                                   .map(SimpleGrantedAuthority::new)
                                                                   .collect(Collectors.toList());

        // create UserDetails, return authentication
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(getKey())
                       .build()
                       .parseClaimsJws(accessToken)
                       .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String parseSubject(String token) {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(getKey())
                       .build()
                       .parseClaimsJws(token)
                       .getBody()
                       .getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("Can't not parse subject in token");
            throw e;
        }
    }

    // verify token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtProperties.AUTHORIZATION_HEADER);
        return resolveToken(bearerToken);
    }

    public String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TokenInfo.GRANT_TYPE)) {
            return bearerToken.substring(TokenInfo.GRANT_TYPE.length() + 1);
        }
        return null;
    }

    public String getRoleFromBearerToken(String bearerToken) {
        String token = resolveToken(bearerToken);

        Claims claims = parseClaims(token);

        return claims.get(JwtProperties.TOKEN_AUTH)
                     .toString();
    }

}
