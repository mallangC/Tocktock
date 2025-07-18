package com.mllg.tocktock.controller;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "https://tock-tock.com")
@RequiredArgsConstructor
@RestController
public class MemberController {

  @GetMapping("/member/profile")
  public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User oauth2User) {
    Map<String, Object> userInfo = new HashMap<>();
    if (oauth2User != null) {
      userInfo.put("name", oauth2User.getAttribute("name"));
      userInfo.put("email", oauth2User.getAttribute("email"));
      userInfo.put("picture", oauth2User.getAttribute("picture"));
      oauth2User.getAuthorities().stream()
              .filter(a -> a.getAuthority().startsWith("ROLE_"))
              .findFirst()
              .ifPresent(authority -> userInfo.put("role", authority.getAuthority().replace("ROLE_", "")));
    } else {
      userInfo.put("message", "로그인되지 않았습니다.");
    }
    return userInfo;
  }

  private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

  @PostMapping("/logout")
  public ResponseEntity<String> performLogout(HttpServletRequest request, HttpServletResponse response,
                                              Authentication authentication) {
    // 현재 인증 정보 가져오기
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      // Spring Security의 로그아웃 핸들러를 사용하여 세션 무효화 및 쿠키 삭제
      logoutHandler.logout(request, response, auth);
      System.out.println("로그아웃 성공: " + auth.getName() + " 세션 무효화 및 JSESSIONID 쿠키 삭제");
    } else {
      System.out.println("로그아웃 요청 수신: 인증된 사용자가 없습니다.");
    }

    return new ResponseEntity<>("Logout successful", HttpStatus.OK);
  }
}
