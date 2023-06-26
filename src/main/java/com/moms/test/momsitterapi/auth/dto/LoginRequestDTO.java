package com.moms.test.momsitterapi.auth.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String userId;
    private String password;
}
