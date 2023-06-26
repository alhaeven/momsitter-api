package com.moms.test.momsitterapi.global.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RequestDTO {

    private Long userIdx;
    private String userId;
    private String password;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd", timezone = "Asia/Seoul")
    private Date birth;
    private String gender;
    private String email;
    private String role;

    // sitter
    private SitterDTO sitter;

    // user
    private UserInfoDTO user;

}






