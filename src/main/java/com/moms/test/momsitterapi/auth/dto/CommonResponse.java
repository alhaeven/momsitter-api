package com.moms.test.momsitterapi.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class CommonResponse {
    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "Asia/Seoul")
    private Date dateTime = new Date();
    private Boolean success;
    private Object response;
    private Object error;
}
