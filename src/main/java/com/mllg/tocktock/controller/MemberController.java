package com.mllg.tocktock.controller;

import com.mllg.tocktock.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();


    @GetMapping("/member/profile")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> userInfo = new HashMap<>();
        if (oauth2User != null) {
            userInfo.put("name", oauth2User.getAttribute("name"));
            userInfo.put("email", oauth2User.getAttribute("email"));
            userInfo.put("picture", oauth2User.getAttribute("picture"));
        } else {
            userInfo.put("message", "로그인되지 않았습니다.");
        }
        return userInfo;
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logoutHandler.logout(request, response, auth);
            log.info("로그아웃 성공: {} & 세션 무효화 및 JSESSIONID 쿠키 삭제", auth.getName());
        } else {
            log.info("로그아웃 요청 수신: 인증된 사용자가 없습니다.");
        }

        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    @DeleteMapping("/member")
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            throw new RuntimeException("OAuth2User is null");
        }
        String email = oauth2User.getAttribute("email");
        return ResponseEntity.ok(memberService.deleteMember(email));
    }
}
