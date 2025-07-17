package com.mllg.tocktock.domain.responseDto;

import com.mllg.tocktock.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {
  private Long id;
  private Boolean isDone;
  private String content;
  private int todoOrder;
  private LocalDateTime completedAt;
  private LocalDateTime createdAt;

  public static TodoDto from (Todo todo){
    return TodoDto.builder()
            .id(todo.getId())
            .isDone(todo.getIsDone())
            .content(todo.getContent())
            .todoOrder(todo.getTodoOrder())
            .completedAt(todo.getCompletedAt())
            .createdAt(todo.getCreatedAt())
            .build();
  }
}
