package com.moms.test.momsitterapi.member.mapper;

import com.moms.test.momsitterapi.global.domain.ChildDTO;
import com.moms.test.momsitterapi.global.domain.FlatRequestDTO;
import com.moms.test.momsitterapi.global.domain.SitterDTO;
import com.moms.test.momsitterapi.member.dto.ResponseMemberInfoDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    @Insert("INSERT INTO sitter VALUES(#{userIdx}, #{coverageMin}, #{coverageMax}, #{intro})")
    int createSitter(FlatRequestDTO addRoleDTO);

    int createChild(Long userIdx, List<ChildDTO> children);

    @Select("SELECT user_idx, user_id, password, role FROM account WHERE user_idx = #{userIdx}")
    Optional<ResponseMemberInfoDTO> findAccountByUserIdx(String userIdx);

    @Select("SELECT user_idx, user_id, role, name, birth, gender, email, req_info FROM account WHERE user_idx = #{userIdx}")
    Optional<ResponseMemberInfoDTO> findAccountInfoByUserIdx(String userIdx);

    @Select("SELECT * FROM child WHERE user_idx = #{userIdx}")
    List<ChildDTO> findChildrenByUserIdx(String userIdx);

    @Select("SELECT coverage_min, coverage_max, intro FROM sitter WHERE user_idx = #{userIdx}")
    SitterDTO findSitterInfoByUserIdx(String userIdx);

    @Update("UPDATE account SET role = #{role}, req_info = #{reqInfo, jdbcType=VARCHAR} WHERE user_idx = #{userIdx}")
    int modifyUserInfoForAddRole(FlatRequestDTO addRoleDTO);

    int modifyUserInfo(String userIdx, Map<String, Object> jsonMap);

    int modifySitter(String userIdx, Map sitterMap);

    int modifyChild(String userIdx, String childIdx, Map child);

    @Delete("DELETE FROM child WHERE user_idx = #{userIdx} AND child_idx = #{childIdx}")
    int deleteChild(String userIdx, String childIdx);
}
