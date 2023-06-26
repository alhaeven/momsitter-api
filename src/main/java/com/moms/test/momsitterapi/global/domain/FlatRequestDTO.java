package com.moms.test.momsitterapi.global.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlatRequestDTO {

    private Long userIdx;
    private String userId;
    private String password;
    private String name;
    private Date birth;
    private String gender;
    private String email;
    private String role;

    private Integer coverageMin; // "3-5" 3
    private Integer coverageMax; // "3-5" 5
    private String intro;

    private List<ChildDTO> children;
    private String reqInfo;

    public static FlatRequestDTO of(RequestDTO dto) {

        FlatRequestDTOBuilder builder = FlatRequestDTO.builder();

        builder.userIdx(dto.getUserIdx())
               .userId(dto.getUserId())
               .password(dto.getPassword())
               .name(dto.getName())
               .birth(dto.getBirth())
               .gender(dto.getGender())
               .email(dto.getEmail())
               .role(dto.getRole());
        if (dto.getSitter() != null) {
            builder.coverageMin(dto.getSitter()
                                   .getCoverageMin())
                   .coverageMax(dto.getSitter()
                                   .getCoverageMax())
                   .intro(dto.getSitter()
                             .getIntro());
        }
        if (dto.getUser() != null) {
            builder.children(dto.getUser()
                                .getChildren())
                   .reqInfo(dto.getUser()
                               .getReqInfo());
        }

        return builder.build();
    }

}
