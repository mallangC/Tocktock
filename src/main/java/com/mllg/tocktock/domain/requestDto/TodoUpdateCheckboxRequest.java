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
public class TodoUpdateCheckboxRequest {
  @NotNull
  private Long id;
  @NotNull
  private Boolean isDone;
}
