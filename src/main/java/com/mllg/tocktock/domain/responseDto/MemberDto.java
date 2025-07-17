package com.mllg.tocktock.domain.responseDto;

import com.mllg.tocktock.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
  private String name;
  private String email;
  private String provider;
  private String oauth2Id;

  public static MemberDto from(Member member){
    return MemberDto.builder()
            .name(member.getName())
            .email(member.getEmail())
            .provider(member.getProvider())
            .oauth2Id(member.getOauth2Id())
            .build();
  }
}
