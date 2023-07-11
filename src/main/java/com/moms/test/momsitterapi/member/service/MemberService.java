package com.moms.test.momsitterapi.member.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.moms.test.momsitterapi.auth.JwtTokenProvider;
import com.moms.test.momsitterapi.global.domain.*;
import com.moms.test.momsitterapi.member.dto.ResponseMemberInfoDTO;
import com.moms.test.momsitterapi.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    static final String STR_role = "role";
    static final String STR_reqInfo = "reqInfo";
    static final String STR_req_info = "req_info";

    static final String STR_coverageMin = "coverageMin";
    static final String STR_coverage_min = "coverage_min";
    static final String STR_coverageMax = "coverageMax";
    static final String STR_coverage_max = "coverage_max";

    static final String STR_children = "children";
    static final String STR_childIdx = "childIdx";

    static final String[] notAllowedKeys = {"userId", "userIdx", "password", "role"};

    static final Map<String, String> convertSnakeMap = new HashMap<String, String>() {{
        put(STR_reqInfo, STR_req_info);
        put(STR_coverageMin, STR_coverage_min);
        put(STR_coverageMax, STR_coverage_max);
    }};

    private final MemberMapper memberMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String addRole(RequestDTO addRoleRequestDTO, String bearerToken) throws Exception {
//        String roles = jwtTokenProvider.getRoleFromBearerToken(bearerToken);

        String userIdx = jwtTokenProvider.getUserIdxFromBearerToken(bearerToken);
        log.debug("user idx from token : {}", userIdx);
        // temp
        Long idx = Long.valueOf(userIdx);
        String roles = memberMapper.findAccountByUserIdx(userIdx)
                                   .get()
                                   .getRole();

        String addRole = addRoleRequestDTO.getRole();
        if (roles.indexOf(addRole) != -1) {
            log.error("Already exist role : {}", addRole);
            throw new InvalidParameterException("Already exist role");
        }

        roles += "," + addRole;
        addRoleRequestDTO.setRole(roles);
        addRoleRequestDTO.setUserIdx(idx);

        FlatRequestDTO dto = FlatRequestDTO.of(addRoleRequestDTO);

        ROLE role = ROLE.valueOf(addRole.toUpperCase());
        int re = 0;
        if (role == ROLE.USER) {
            if (dto.getChildren() != null && dto.getChildren()
                                                .size() != 0) {
                createChildren(dto.getUserIdx(), dto.getChildren());
            }
        } else if (role == ROLE.SITTER) {
            re = memberMapper.createSitter(dto);

            if (re != 1) {
                throw new Exception("internal server error in create sitter");
            }
        }

        // update account role and optional request info
        re = memberMapper.modifyUserInfoForAddRole(dto);
        if (re != 1) {
            throw new Exception("internal server error in update account");
        }

        return userIdx;
    }

    @Transactional
    public String modifyInfo(JsonNode payload, String bearerToken) {
        String userIdx = jwtTokenProvider.getUserIdxFromBearerToken(bearerToken);
        log.debug("user idx from token : {}", userIdx);

        // extract json
        ObjectMapper jsonMapper = new JsonMapper();
        ObjectNode updateInfoJson = (ObjectNode) payload;
        ObjectNode userAddInfoJson = null;
        ObjectNode sitterJson = null;
        for (String r : updateInfoJson.remove(STR_role).asText().split(",")) {
            ROLE role = ROLE.valueOf(r.toUpperCase());
            if (payload.has(r)) {
                if (role == ROLE.USER) {
                    userAddInfoJson = (ObjectNode) updateInfoJson.remove(r);
                } else if (role == ROLE.SITTER) {
                    sitterJson = (ObjectNode) updateInfoJson.remove(r);
                }
            }
        }

        ArrayNode childrenJson = null;
        if (userAddInfoJson != null) {

            // 역정규화로 인한 req info 이동
            if (updateInfoJson.has(STR_reqInfo)) {
                updateInfoJson.put(convertSnakeMap.get(STR_reqInfo), userAddInfoJson.remove(STR_reqInfo));
            }

            if (userAddInfoJson.has(STR_children)) {
                childrenJson = (ArrayNode) userAddInfoJson.get(STR_children);
            }
        }

        // camel to snake
        if (sitterJson != null) {
            for (Map.Entry<String, String> entry : convertSnakeMap.entrySet()) {
                if (sitterJson.has(entry.getKey())) {
                    sitterJson.put(entry.getValue(), sitterJson.remove(entry.getKey()));
                }
            }
        }

        // update 불가 key 지우기
        for (String key : notAllowedKeys) {
            if (updateInfoJson.has(key)) {
                updateInfoJson.remove(key);
            }
        }
        log.debug("account update info : {}", updateInfoJson);

        // update account
        memberMapper.modifyUserInfo(userIdx, jsonMapper.convertValue(updateInfoJson, HashMap.class));
        log.debug("success update account");


        // update sitter
        if (sitterJson != null) {
            log.debug("sitter update info : {}", sitterJson);
            memberMapper.modifySitter(userIdx, jsonMapper.convertValue(sitterJson, HashMap.class));
            log.debug("success update sitter");
        }

        // update child
        if (childrenJson != null) {
            log.debug("child update array : {}", childrenJson);
            List<Map> children = jsonMapper.convertValue(childrenJson, ArrayList.class);

            for (Map child : children) {
                // child index 유무 검사
                if (!child.containsKey(STR_childIdx) || child.get(STR_childIdx) == null) {
                    throw new InvalidParameterException("Non exist child index");
                }
                log.debug("update child info : {}", child);
                String childIdx = child.remove(STR_childIdx).toString();
                memberMapper.modifyChild(userIdx, childIdx, child);
            }
            log.debug("success update children");
        }

        return userIdx;
    }

    public ResponseMemberInfoDTO getMemberInfo(String bearerToken) throws Exception {
        String userIdx = jwtTokenProvider.getUserIdxFromBearerToken(bearerToken);
        log.debug("user idx from token : {}", userIdx);

        Optional<ResponseMemberInfoDTO> responseDTOOpt = memberMapper.findAccountInfoByUserIdx(userIdx);

        if (!responseDTOOpt.isPresent()) {
            log.warn("Internal Server Error / Fail to member info by userIdx : {}", userIdx);
            throw new Exception("Internal Server Error / Fail to member info by userIdx");
        }

        ResponseMemberInfoDTO response = responseDTOOpt.get();

        for (String roleStr : response.getRole().split(",")) {

            ROLE role = ROLE.valueOf(roleStr.toUpperCase());
            if (role == ROLE.USER) {
                List<ChildDTO> children = memberMapper.findChildrenByUserIdx(userIdx);

                if (children == null) {
                    children = new ArrayList<>();
                }

                UserInfoDTO additionalInfo = new UserInfoDTO(children, response.getReqInfo());
                response.setUser(additionalInfo);
            } else if (role == ROLE.SITTER) {
                SitterDTO sitter = memberMapper.findSitterInfoByUserIdx(userIdx);

                response.setSitter(sitter);
            }
        }
        log.debug("get member info : {}", response);

        return response;
    }

    public void createChildren(Long userIdx, List<ChildDTO> children) throws Exception {
        int re = memberMapper.createChild(userIdx, children);

        log.debug("create child result : {}", re);

        if (re != children.size()) {
            throw new Exception("internal server error in create children");
        }
    }

    public void createChildren(List<ChildDTO> children, String bearerToken) throws Exception {
        String userIdx = jwtTokenProvider.getUserIdxFromBearerToken(bearerToken);
        log.debug("user idx from token : {}", userIdx);

        createChildren(Long.valueOf(userIdx), children);
    }

    public void deleteChild(String childIdx, String bearerToken) throws Exception {
        String userIdx = jwtTokenProvider.getUserIdxFromBearerToken(bearerToken);
        log.debug("user idx from token : {}", userIdx);

        int re = memberMapper.deleteChild(userIdx, childIdx);
        log.debug("delete child result : {}", re);

        if (re != 1) {
            throw new Exception("internal server error in delete children");
        }
    }

}
