package com.mllg.tocktock.domain.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodolistDto {
    List<TodoDto> todoList;

    public static TodolistDto from(List<TodoDto> todoList){
        return TodolistDto.builder()
                .todoList(todoList)
                .build();
    }
}
