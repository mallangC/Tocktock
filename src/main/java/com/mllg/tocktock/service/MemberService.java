package com.mllg.tocktock.service;

import com.mllg.tocktock.entity.Member;
import com.mllg.tocktock.exception.CustomException;
import com.mllg.tocktock.exception.ErrorCode;
import com.mllg.tocktock.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  public String deleteMember(String email) {
    Member member = memberRepository.findByEmail(email)
            .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    memberRepository.delete(member);

    return "회원 탈퇴 완료";
  }
}
