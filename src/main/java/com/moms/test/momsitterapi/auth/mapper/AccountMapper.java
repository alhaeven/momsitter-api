package com.moms.test.momsitterapi.auth.mapper;

import com.moms.test.momsitterapi.auth.dto.Account;
import com.moms.test.momsitterapi.global.domain.ChildDTO;
import com.moms.test.momsitterapi.global.domain.FlatRequestDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AccountMapper {

    @Select("select 1")
    int test();
    @Select("SELECT user_idx, user_id, password, role FROM account WHERE user_id = #{userId}")
    Optional<Account> findAccountByUserId(String userId);

    @Insert("INSERT INTO account(user_id, password, role, name, birth, gender, email, req_info) VALUES(#{userId}, #{password}, #{role}, #{name}, #{birth}, #{gender}, #{email}, #{reqInfo, jdbcType=VARCHAR})")
    int createUserAccount(FlatRequestDTO flatRequestDTO);

    @Insert("INSERT INTO sitter VALUES(#{userIdx}, #{coverageMin}, #{coverageMax}, #{intro})")
    int createSitter(FlatRequestDTO flatRequestDTO);

    int createChild(Long userIdx, List<ChildDTO> children);

}
