package com.moms.test.momsitterapi.member.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moms.test.momsitterapi.auth.dto.CommonResponse;
import com.moms.test.momsitterapi.global.domain.RequestDTO;
import com.moms.test.momsitterapi.global.domain.UserInfoDTO;
import com.moms.test.momsitterapi.member.dto.ResponseMemberInfoDTO;
import com.moms.test.momsitterapi.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/member")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Member", description = "회원 정보 수정, 추가, 삭제등")
public class MemberController {

    private final MemberService memberService;

//    @Operation(summary = "Member", description = "add Role")
    @PutMapping("/{userIdx}")
    public ResponseEntity addRole(@PathVariable("userIdx") String userIdx, @RequestBody RequestDTO requestDTO
            /*,@RequestHeader(JwtProperties.AUTHORIZATION_HEADER) String bearerToken*/) {

        log.info("addRole method in MemeberController");
        log.debug("addRole request : {}", requestDTO);
        ResponseEntity responseEntity = null;
        try {
            memberService.addRole(userIdx, requestDTO);
            log.info("success add role / idx : {}", userIdx);

            CommonResponse response = CommonResponse.builder()
                                                    .success(true)
                                                    .response(String.format("success add role : %s", userIdx))
                                                    .build();

            // 202
            responseEntity = new ResponseEntity(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error(e.getMessage());

            CommonResponse response = CommonResponse.builder()
                                                    .success(false)
                                                    .response("server error")
                                                    .build();
            // 500
            responseEntity = new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @PatchMapping("/{userIdx}")
    public ResponseEntity modifyInfo(@PathVariable("userIdx") String userIdx, @RequestBody JsonNode payload) {
        log.info("modifyInfo method in MemeberController");
        log.debug("modifyInfo userIdx : {} / request payload: {}", userIdx, payload);
        ResponseEntity responseEntity = null;
        try {
            memberService.modifyInfo(userIdx, payload);
            log.info("success modify info | idx : {}", userIdx);

            CommonResponse response = CommonResponse.builder()
                                                    .success(true)
                                                    .response(String.format("success modify info : %s", userIdx))
                                                    .build();

            // 202
            responseEntity = new ResponseEntity(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error(e.getMessage());

            CommonResponse response = CommonResponse.builder()
                                                    .success(false)
                                                    .response("server error")
                                                    .build();
            // 500
            responseEntity = new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @GetMapping("/{userIdx}")
    public ResponseEntity getInfo(@PathVariable("userIdx") String userIdx) {
        log.info("getInfo method in MemberController");
        log.debug("userIdx resource : {}", userIdx);

        ResponseEntity responseEntity = null;
        try {
            ResponseMemberInfoDTO responseDTO = memberService.getMemberInfo(userIdx);

            log.info("success get info / {}", new ObjectMapper().writeValueAsString(responseDTO));

            CommonResponse response = CommonResponse.builder()
                                                    .success(true)
                                                    .response(responseDTO)
                                                    .build();

            // 201
            responseEntity = new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());

            CommonResponse response = CommonResponse.builder()
                                                    .success(false)
                                                    .response("server error")
                                                    .build();
            // 500
            responseEntity = new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @PutMapping("/{userIdx}/child")
    public ResponseEntity addChildren(@PathVariable("userIdx") String userIdx, @RequestBody UserInfoDTO requestDTO) {
        log.info("add children method in MemberController");
        log.debug("add children userIdx : {} / request : {}", userIdx, requestDTO);

        ResponseEntity responseEntity = null;
        try {
            memberService.createChildren(userIdx, requestDTO.getChildren());

            CommonResponse response = CommonResponse.builder()
                                                    .success(true)
                                                    .response("success create children")
                                                    .build();

            // 201
            responseEntity = new ResponseEntity(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());

            CommonResponse response = CommonResponse.builder()
                                                    .success(false)
                                                    .response("server error")
                                                    .build();
            // 500
            responseEntity = new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @DeleteMapping("/{userIdx}/child/{childIdx}")
    public ResponseEntity deleteChild(@PathVariable("userIdx") String userIdx,
                                      @PathVariable("childIdx") String childIdx) {
        log.info("delete children method in MemberController");
        log.debug("delete children userIdx : {} / childIdx : {}", userIdx, childIdx);

        ResponseEntity responseEntity = null;
        try {
            memberService.deleteChild(userIdx, childIdx);

            CommonResponse response = CommonResponse.builder()
                                                    .success(true)
                                                    .response("success delete child")
                                                    .build();

            // 201
            responseEntity = new ResponseEntity(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error(e.getMessage());

            CommonResponse response = CommonResponse.builder()
                                                    .success(false)
                                                    .response("server error")
                                                    .build();
            // 500
            responseEntity = new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }


}
