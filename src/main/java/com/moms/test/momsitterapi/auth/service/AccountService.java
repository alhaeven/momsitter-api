package com.moms.test.momsitterapi.auth.service;

import com.moms.test.momsitterapi.auth.JwtTokenProvider;
import com.moms.test.momsitterapi.auth.dto.Account;
import com.moms.test.momsitterapi.auth.dto.TokenInfo;
import com.moms.test.momsitterapi.auth.mapper.AccountMapper;
import com.moms.test.momsitterapi.auth.util.ValidateUtil;
import com.moms.test.momsitterapi.global.domain.FlatRequestDTO;
import com.moms.test.momsitterapi.global.domain.ROLE;
import com.moms.test.momsitterapi.global.domain.RequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService implements UserDetailsService {

    private final AccountMapper accountMapper;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("execute loadUserByUsername : {}", username);

        Account account = accountMapper.findAccountByUserId(username)
                                       // test temp code
//                                        .map(x -> {x.setPassword(passwordEncoder.encode(x.getPassword())); return x;})
                                       .orElseThrow(() -> {
                                           log.error("Not Found User {}", username);
                                           return new UsernameNotFoundException(
                                                   String.format("Not Found User '%s'", username));
                                       });
        log.debug("Get user Account : {}", account);

        return account;
    }


    public TokenInfo login(String username, String password) throws AuthenticationException {
        // Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);

        // 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject()
                                                                    .authenticate(authenticationToken);

        // 인증 정보를 기반으로 JWT 토큰 생성
        return tokenProvider.generateToken(authentication);
    }

    @Transactional
    public String join(RequestDTO joinRequestDTO) throws Exception {
        if (!ValidateUtil.validatePassword(joinRequestDTO.getPassword())) {
            throw new InvalidParameterException(String.format("Invalid password"));
        }

        if (accountMapper.findAccountByUserId(joinRequestDTO.getUserId())
                         .isPresent()) {
            throw new DuplicateKeyException(String.format("Duplicate id : %s", joinRequestDTO.getUserId()));
        }

        // crypt password
        joinRequestDTO.setPassword(passwordEncoder.encode(joinRequestDTO.getPassword()));

        // convert dto
        FlatRequestDTO dto = FlatRequestDTO.of(joinRequestDTO);

        // create account
        int re = accountMapper.createUserAccount(dto);

        log.debug("create account user result : {}", re);

        if (re != 1) {
            throw new Exception("internal server error in create user account");
        }

        // confirm new account
        Account newAccount = accountMapper.findAccountByUserId(joinRequestDTO.getUserId())
                                          .get();
        Long userIdx = newAccount.getUserIdx();
        dto.setUserIdx(userIdx);

        log.debug("create account index : {}", userIdx);

        // create role
        ROLE role = ROLE.valueOf(joinRequestDTO.getRole()
                                               .toUpperCase());
        if (role == ROLE.USER) {
            if (dto.getChildren() != null && dto.getChildren()
                                                .size() != 0) {
                re = accountMapper.createChild(userIdx, dto.getChildren());

                log.debug("create child result : {}", re);

                if (re != dto.getChildren()
                             .size()) {
                    throw new Exception("internal server error in create children");
                }
            }
        } else if (role == ROLE.SITTER) {
            re = accountMapper.createSitter(dto);

            if (re != 1) {
                throw new Exception("internal server error in create sitter");
            }
        }

        return newAccount.getUserId();
    }

    public int test() {
        return accountMapper.test();
    }


//    public String refreshAccessToken(String refreshToken){
//        String username = tokenProvider.parseSubject(refreshToken);
//        return tokenProvider.generateAccessToken(username, Account.defaultAuthorities());
//    }
}
