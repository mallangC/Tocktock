package com.mllg.tocktock.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
  ADMIN("ROLE_ADMIN", "관리자"),
  USER("ROLE_USER", "회원");

  private final String key;
  private final String title;
}
