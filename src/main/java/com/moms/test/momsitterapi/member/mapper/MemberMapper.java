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
    Optional<ResponseMemberInfoDTO> findAccountByUserIdx(Long userIdx);

    @Select("SELECT user_idx, user_id, role, name, birth, gender, email, req_info FROM account WHERE user_idx = #{userIdx}")
    Optional<ResponseMemberInfoDTO> findAccountInfoByUserIdx(Long userIdx);

    @Select("SELECT * FROM child WHERE user_idx = #{userIdx}")
    List<ChildDTO> findChildrenByUserIdx(Long userIdx);

    @Select("SELECT coverage_min, coverage_max, intro FROM sitter WHERE user_idx = #{userIdx}")
    SitterDTO findSitterInfoByUserIdx(Long userIdx);

    @Update("UPDATE account SET role = #{role}, req_info = #{reqInfo, jdbcType=VARCHAR} WHERE user_idx = #{userIdx}")
    int modifyUserInfoForAddRole(FlatRequestDTO addRoleDTO);

    int modifyUserInfo(Long userIdx, Map<String, Object> jsonMap);

    int modifySitter(Long userIdx, Map sitterMap);

    int modifyChild(Long userIdx, Long childIdx, Map child);

    @Delete("DELETE FROM child WHERE user_idx = #{userIdx} AND child_idx = #{childIdx}")
    int deleteChild(Long userIdx, Long childIdx);
}
