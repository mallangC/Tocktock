package com.mllg.tocktock.service;

import com.mllg.tocktock.entity.Member;
import com.mllg.tocktock.exception.CustomException;
import com.mllg.tocktock.exception.ErrorCode;
import com.mllg.tocktock.repository.MemberRepository;
import com.mllg.tocktock.type.MemberType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final MemberRepository memberRepository;


  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

    // OAuth2UserRequest를 통해 사용자 정보 로드
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    // 현재 로그인 진행 중인 서비스를 구분하는 ID (google, naver, kakao 등)
    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    // OAuth2 로그인 진행 시 키가 되는 필드 값 (Primary Key와 같은 의미)
    // 구글의 경우 기본적으로 'sub'이지만, 네이버/카카오 등은 다를 수 있음
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
            .getUserInfoEndpoint().getUserNameAttributeName();

    Map<String, Object> attributes = oAuth2User.getAttributes();

    if (!"google".equals(registrationId)) {
      throw new CustomException(ErrorCode.NOT_MATCHED_GOOGLE);
    }

    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String oauth2Id = (String) attributes.get("sub");
    log.info("email: {}, name: {}, oauth2Id: {}", email, name, oauth2Id);

    boolean isExists = memberRepository.existsByEmail(email);
    if (!isExists) {
      Member member = Member.builder()
              .email(email)
              .name(name)
              .provider(registrationId)
              .oauth2Id(oauth2Id)
              .role(MemberType.USER)
              .build();

      memberRepository.save(member);
    }

    return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            userNameAttributeName);

  }
}
