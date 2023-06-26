package com.moms.test.momsitterapi.global.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ROLE {
    USER, SITTER, ADMIN;

    @JsonCreator
    public static ROLE from(String role) {
        return ROLE.valueOf(role.toUpperCase());
    }
}
