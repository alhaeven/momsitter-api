package com.moms.test.momsitterapi.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.moms.test.momsitterapi.global.domain.SitterDTO;
import com.moms.test.momsitterapi.global.domain.UserInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ResponseMemberInfoDTO {

    private Long userIdx;
    private String userId;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd", timezone = "Asia/Seoul")
    private Date birth;
    private String gender;
    private String email;
    private String role;

    @JsonIgnore
    private String reqInfo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SitterDTO sitter;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserInfoDTO user;

}
