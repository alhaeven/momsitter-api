package com.moms.test.momsitterapi.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moms.test.momsitterapi.auth.config.JwtProperties;
import com.moms.test.momsitterapi.auth.dto.CommonResponse;
import com.moms.test.momsitterapi.auth.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("authentication failed / {}", HttpStatus.UNAUTHORIZED.name());
        // 401
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorResponse error = ErrorResponse.builder()
                                           .status(HttpStatus.UNAUTHORIZED.value())
                                           .message(HttpStatus.UNAUTHORIZED.name())
                                           .code(JwtProperties.TOKEN_AUTH)
                                           .build();

        response.setContentType(JwtProperties.APPLICATION_JSON_CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream()
                .println(jsonMapper.writeValueAsString(CommonResponse.builder()
                                                                     .success(false)
                                                                     .error(error).build()));
    }
}
