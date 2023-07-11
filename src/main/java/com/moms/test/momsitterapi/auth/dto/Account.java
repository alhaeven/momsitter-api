package com.moms.test.momsitterapi.auth.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public class Account implements UserDetails {

    private static final String STR_DEFAULT_ROLE = "USER";

    private String userIdxStr;
    private Long userIdx;
    private String userId;
    private String password;
    private String role = STR_DEFAULT_ROLE;

    private Collection<? extends GrantedAuthority> authorities;

    private Collection<? extends GrantedAuthority> generateAuthorities(String role) {
        return Arrays.stream(role.split(","))
                     .map(SimpleGrantedAuthority::new)
                     .collect(Collectors.toList());

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            this.authorities = generateAuthorities(this.role);
        }

        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        if(userIdxStr == null) {
            userIdxStr = userIdx + "";
        }
        return this.userIdxStr;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
