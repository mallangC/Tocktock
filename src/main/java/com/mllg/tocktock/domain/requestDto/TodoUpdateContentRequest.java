package com.mllg.tocktock.domain.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoUpdateContentRequest {
    @NotNull
    private Long id;
    @NotBlank(message = "내용을 입력해주세요")
    @Size(min = 1, max = 80, message = "내용은 1자 이상 80자 이하여야 합니다.")
    private String content;
}
