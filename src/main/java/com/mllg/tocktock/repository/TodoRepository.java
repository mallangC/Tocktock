package com.mllg.tocktock.repository;

import com.mllg.tocktock.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> , TodoRepositoryCustom{
}
