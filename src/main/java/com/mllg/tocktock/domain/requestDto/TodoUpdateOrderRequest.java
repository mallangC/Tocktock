package com.mllg.tocktock.domain.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoUpdateOrderRequest {
    @NotNull(message = "드래그한 아이디를 입력해주세요")
    private Long draggedId;
    @NotNull(message = "타겟 아이디를 입력해주세요")
    private Long targetId;
}
