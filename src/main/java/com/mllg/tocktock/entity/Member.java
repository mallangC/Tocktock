package com.mllg.tocktock.entity;

import com.mllg.tocktock.type.MemberType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String email;
  private String provider;
  @Column(name = "oauth2_id")
  private String oauth2Id;
  @Enumerated(EnumType.STRING)
  private MemberType role;
  @OneToMany(mappedBy = "member", orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Todo> todoList;

}
