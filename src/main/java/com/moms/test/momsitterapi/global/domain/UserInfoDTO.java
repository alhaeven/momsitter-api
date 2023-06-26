package com.moms.test.momsitterapi.global.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    private List<ChildDTO> children;
    private String reqInfo;
}
