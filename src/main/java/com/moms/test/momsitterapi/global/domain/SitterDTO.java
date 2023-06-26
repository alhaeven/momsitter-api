package com.moms.test.momsitterapi.global.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SitterDTO {

    private Integer coverageMin = 0; // "3-5" 3
    private Integer coverageMax = 200; // "3-5" 5
    private String intro;
}
