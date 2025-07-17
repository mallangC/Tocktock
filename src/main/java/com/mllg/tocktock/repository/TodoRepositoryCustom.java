package com.mllg.tocktock.repository;

import com.mllg.tocktock.entity.Todo;

import java.util.List;

public interface TodoRepositoryCustom {
    List<Todo> searchAllTodoByToday(String email);
    List<Todo> searchAllTodoCompleted(String email);

}
