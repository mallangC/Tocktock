package com.mllg.tocktock.repository;

import com.mllg.tocktock.entity.Todo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.mllg.tocktock.entity.QTodo.todo;

@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Todo> searchAllTodoByToday(String email) {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = LocalDate.now().atTime(LocalTime.MAX);

        BooleanExpression isCompletedToday = todo.completedAt.goe(today)
                .and(todo.completedAt.lt(tomorrow));
        BooleanExpression isIncomplete = todo.isDone.eq(false);

        return queryFactory.selectFrom(todo)
                .join(todo.member).fetchJoin()
                .where((isCompletedToday
                        .or(isIncomplete))
                        .and(todo.member.email.eq(email)))
                .orderBy(todo.todoOrder.asc())
                .fetch();
    }

    @Override
    public List<Todo> searchAllTodoCompleted(String email) {
        LocalDateTime today = LocalDate.now().atStartOfDay();

        BooleanExpression isCompletedNotToday = todo.completedAt.lt(today);
        BooleanExpression isIncomplete = todo.isDone.eq(true);

        return queryFactory.selectFrom(todo)
                .join(todo.member).fetchJoin()
                .where(isCompletedNotToday
                        .and(isIncomplete)
                        .and(todo.member.email.eq(email)))
                .orderBy(todo.completedAt.desc())
                .fetch();
    }
}