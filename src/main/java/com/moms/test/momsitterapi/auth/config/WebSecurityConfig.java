package com.moms.test.momsitterapi.auth.config;

import com.moms.test.momsitterapi.auth.JwtAuthenticationFilter;
import com.moms.test.momsitterapi.auth.JwtTokenProvider;
import com.moms.test.momsitterapi.auth.handler.JwtAccessDeniedHandler;
import com.moms.test.momsitterapi.auth.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    public static String[] ALLOW_URLS = {
            "api/account/test", "/api/account/login", "/api/account/join",
    };

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session disable

                .and()
                .authorizeRequests()
                .antMatchers(ALLOW_URLS).permitAll()
                .antMatchers("/auth/test").hasRole("USER") // test
                .anyRequest().authenticated()

                .and()
                .exceptionHandling()
                .accessDeniedHandler(new JwtAccessDeniedHandler())
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        // allow swagger
        return (web) -> web.ignoring().antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html");
    }

}
