package com.mllg.tocktock.controller;

import com.mllg.tocktock.entity.Member;
import com.mllg.tocktock.repository.MemberRepository;
import com.mllg.tocktock.type.MemberType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    private final String name = "integration_user";
    private final String email = "integration@email.com";
    private final String picture = "https://integration.com/integration.jpg";

    private DefaultOAuth2User createMockOAuth2User() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", name);
        attributes.put("email", email);
        attributes.put("picture", picture);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "name"
        );
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getUserInfoSuccess() throws Exception {
        //given
        DefaultOAuth2User principal = createMockOAuth2User();

        // when & then
        mockMvc.perform(get("/member/profile")
                        .with(oauth2Login().oauth2User(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.picture").value(picture));
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 회원 인증 실패(로그인 상태가 아님)")
    void getUserInfoFailure() throws Exception {
        //given
        // when & then
        mockMvc.perform(get("/member/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() throws Exception {
        //given
        //when & then
        mockMvc.perform(post("/logout").with(oauth2Login().oauth2User(createMockOAuth2User()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("JSESSIONID", 0));
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteMemberSuccess() throws Exception {
        //given
        memberRepository.save(Member.builder()
                        .email(email)
                        .name(name)
                        .provider("google")
                        .todoList(new ArrayList<>())
                        .oauth2Id("12305139827")
                        .role(MemberType.USER)
                        .build());

        assertThat(memberRepository.findByEmail(email)).isPresent();

        //when & then
        mockMvc.perform(delete("/member")
                        .with(oauth2Login().oauth2User(createMockOAuth2User()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("회원 탈퇴 완료"));
    }

}