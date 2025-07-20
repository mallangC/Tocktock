package com.mllg.tocktock.domain.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoAddRequest {
  @NotBlank(message = "내용을 입력해주세요")
  @Size(min = 1, max = 60, message = "내용은 1자 이상 60자 이하여야 합니다.")
  private String content;
}
