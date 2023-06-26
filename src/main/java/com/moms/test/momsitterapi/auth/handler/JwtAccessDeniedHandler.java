package com.moms.test.momsitterapi.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moms.test.momsitterapi.auth.config.JwtProperties;
import com.moms.test.momsitterapi.auth.dto.CommonResponse;
import com.moms.test.momsitterapi.auth.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("Required authentication authority does not exist");
        // 403
        ErrorResponse error = ErrorResponse.builder()
                                           .status(HttpStatus.FORBIDDEN.value())
                                           .message(HttpStatus.FORBIDDEN.toString())
                                           .code(JwtProperties.TOKEN_AUTH)
                                           .build();

        response.setContentType(JwtProperties.APPLICATION_JSON_CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getOutputStream()
                .println(jsonMapper.writeValueAsString(CommonResponse.builder()
                                                                     .success(false)
                                                                     .error(error)
                                                                     .build()));
//        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
