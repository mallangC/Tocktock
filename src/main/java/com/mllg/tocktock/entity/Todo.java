package com.mllg.tocktock.entity;

import com.mllg.tocktock.domain.requestDto.TodoAddRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo extends BaseEntity implements Comparable<Todo>{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;
  private Boolean isDone;
  private String content;
  private int todoOrder;
  private LocalDateTime completedAt;

  @Override
  public int compareTo(Todo o) {
    return Integer.compare(this.todoOrder, o.todoOrder);
  }

  public void updateOrder(int orderNum){
    this.todoOrder = orderNum;
  }

  public void updateChecked(Boolean isDone, int orderNum){
    this.isDone = isDone;
    this.todoOrder = orderNum;
    if (!isDone){
      this.completedAt = null;
      return;
    }
    this.completedAt = LocalDateTime.now();
  }

  public void updateContent(String content){
    this.content = content;
  }

  public static Todo from(Member member, TodoAddRequest request, int orderNum){
    return Todo.builder()
            .member(member)
            .isDone(false)
            .content(request.getContent())
            .todoOrder(orderNum)
            .completedAt(null)
            .build();
  }
}
