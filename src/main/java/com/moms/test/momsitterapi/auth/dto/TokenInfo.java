package com.moms.test.momsitterapi.auth.dto;

import lombok.*;

@Builder
@Getter
@ToString
@AllArgsConstructor
public class TokenInfo {

    public static final String GRANT_TYPE = "Bearer";

    private final String accessToken;
//    private final String refreshToken;
}
