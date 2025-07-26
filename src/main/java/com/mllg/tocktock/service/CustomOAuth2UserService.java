package com.mllg.tocktock.service;

import com.mllg.tocktock.entity.Member;
import com.mllg.tocktock.repository.MemberRepository;
import com.mllg.tocktock.type.MemberType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final MemberRepository memberRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 이 객체에는 ID Token의 클레임(사용자 정보)이 담겨 있습니다.
        OidcUser oidcUser = super.loadUser(userRequest);
        // [2] 현재 로그인 중인 서비스(구글, 네이버 등)를 구분하는 registrationId를 가져옵니다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // [3] ID Token 클레임(claims)에서 사용자 정보를 추출합니다.
        Map<String, Object> claims = oidcUser.getClaims();
        String email = (String) claims.get("email");
        String name = (String) claims.get("name");
        String oauth2Id = (String) claims.get("sub"); // 구글의 'sub' 클레임을 oauth2Id로 사용

        log.info("OAuth2 Provider: {}, email: {}, name: {}, oauth2Id: {}", registrationId, email, name, oauth2Id);

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
        return oidcUser;
    }
}
