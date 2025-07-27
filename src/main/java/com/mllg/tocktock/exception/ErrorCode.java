package com.mllg.tocktock.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST , "회원을 찾을 수 없습니다"),

  NOT_MATCHED_GOOGLE(HttpStatus.BAD_REQUEST , "구글 로그인이 아닙니다"),

  NOT_FOUND_TODO(HttpStatus.BAD_REQUEST , "할 일을 찾을 수 없습니다"),
  CANNOT_DELETE_TODO_COMPLETED_BEFORE_TODAY(HttpStatus.BAD_REQUEST , "오늘 이전에 완료된 할 일은 삭제할 수 없습니다."),




  ;

  private final HttpStatus httpStatus;
  private final String message;
}
