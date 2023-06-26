package com.moms.test.momsitterapi.auth.controller;

import com.moms.test.momsitterapi.auth.config.JwtProperties;
import com.moms.test.momsitterapi.auth.dto.CommonResponse;
import com.moms.test.momsitterapi.auth.dto.TokenInfo;
import com.moms.test.momsitterapi.auth.service.AccountService;
import com.moms.test.momsitterapi.global.domain.RequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
@Slf4j
@Tag(name = "Authentication", description = "회원가입, 로그인, 토큰 발행")
public class AuthController {

    private final AccountService accountService;

    @Operation(hidden = true)
    @GetMapping("/test")
    public String test() {
        return accountService.test() + "";
    }

    @PostMapping("/join")
    public ResponseEntity join(@RequestBody RequestDTO joinRequestDTO) {
        log.info("join method in AuthController");
        log.debug("join request : {}", joinRequestDTO);

        ResponseEntity responseEntity = null;
        try {
            String id = accountService.join(joinRequestDTO);
            log.info("success join / id : {}", id);

            CommonResponse response = CommonResponse.builder()
                                                    .success(true)
                    .response(String.format("success join : %s", id))
                                                    .build();

            // 201
            responseEntity = new ResponseEntity(response, HttpStatus.CREATED);
        } catch (InvalidParameterException e) {
            log.error(e.getMessage());

            CommonResponse response = CommonResponse.builder()
                                                    .success(false)
                                                    .response("invalid password")
                                                    .build();

            // 400
            responseEntity = new ResponseEntity(response, HttpStatus.BAD_REQUEST);

        } catch (DuplicateKeyException e) {
            log.error(e.getMessage());

            CommonResponse response = CommonResponse.builder()
                                                    .success(false)
                                                    .response("duplicate user id")
                                                    .build();
            // 503
            responseEntity = new ResponseEntity(response, HttpStatus.SERVICE_UNAVAILABLE);
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

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody RequestDTO loginDTO) {
        log.info("login method in AuthController");
        log.debug("login request : {}", loginDTO);

        TokenInfo tokenInfo = accountService.login(loginDTO.getUserId(), loginDTO.getPassword());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtProperties.AUTHORIZATION_HEADER, String.format("%s %s", TokenInfo.GRANT_TYPE, tokenInfo.getAccessToken()));

        CommonResponse responseObj = CommonResponse.builder()
                                                   .response(tokenInfo)
                                                   .success(true)
                                                   .build();

        return new ResponseEntity(responseObj, httpHeaders, HttpStatus.OK);

    }


}
