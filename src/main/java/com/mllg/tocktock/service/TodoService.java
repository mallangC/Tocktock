package com.mllg.tocktock.service;

import com.mllg.tocktock.domain.requestDto.TodoAddRequest;
import com.mllg.tocktock.domain.requestDto.TodoUpdateCheckboxRequest;
import com.mllg.tocktock.domain.requestDto.TodoUpdateContentRequest;
import com.mllg.tocktock.domain.responseDto.TodoDto;
import com.mllg.tocktock.entity.Member;
import com.mllg.tocktock.entity.Todo;
import com.mllg.tocktock.exception.CustomException;
import com.mllg.tocktock.exception.ErrorCode;
import com.mllg.tocktock.repository.MemberRepository;
import com.mllg.tocktock.repository.TodoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class TodoService {
    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;

    public List<TodoDto> getAllTodo(String email) {
        existsMemberByEmail(email);
        List<Todo> todolist = todoRepository.searchAllTodoByToday(email);
        if (todolist.isEmpty()) {
            return new ArrayList<>();
        }

        return todolist.stream().map(TodoDto::from).toList();
    }

    public TodoDto addTodo(String email, TodoAddRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        List<Todo> todolist = todoRepository.searchAllTodoByToday(email);
        if (!todolist.isEmpty()) {
            int index = 1;
            for (Todo todo : todolist) {
                todo.updateOrder(index++);
            }
        }

        Todo todo = todoRepository.save(Todo.from(member, request, 0));
        return TodoDto.from(todo);
    }

    @Transactional
    public List<TodoDto> updateTodoCheckbox(String email, TodoUpdateCheckboxRequest request) {
        existsMemberByEmail(email);
        List<Todo> todolist = todoRepository.searchAllTodoByToday(email);
        if (todolist.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_TODO);
        }

        int index = 0;
        if (request.getIsDone()) {
            int startDoneNum = findMaxByMemberToOrder(todolist);
            Collections.sort(todolist);

            // isDone이 시작되는 인덱스 찾기
            for (Todo todo : todolist) {
                if (todo.getIsDone()) {
                    startDoneNum = todo.getTodoOrder() - 1;
                    break;
                }
            }
            // request에 id의 객체를 찾아서 order를 isDone이 시작되는 인덱스 -1로 변경
            // isDone = false 인 객체는 order 0부터 차례대로 증가하며 변경
            for (Todo todo : todolist) {
                if (todo.getIsDone()) {
                    break;
                }
                if (Objects.equals(todo.getId(), request.getId())) {
                    todo.updateChecked(request.getIsDone(), startDoneNum);
                    continue;
                }
                todo.updateOrder(index++);
            }
        } else {
            index = 1;
            // request에 id의 객체를 찾아서 order를 0으로 변경
            // 전체 객체는 order 1부터 차례대로 증가하며 변경
            for (Todo todo : todolist) {
                if (Objects.equals(todo.getId(), request.getId())) {
                    todo.updateChecked(request.getIsDone(), 0);
                    continue;
                }
                todo.updateOrder(index++);
            }
        }
        Collections.sort(todolist);

        return todolist.stream().map(TodoDto::from).toList();
    }

    @Transactional
    public List<TodoDto> updateTodoContent(String email, TodoUpdateContentRequest request) {
        existsMemberByEmail(email);

        List<Todo> todolist = todoRepository.searchAllTodoByToday(email);
        if (todolist.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_TODO);
        }

        Optional<Todo> findTodo = todolist.stream()
                .filter(todo -> todo.getId().equals(request.getId()))
                .findFirst();

        if (findTodo.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_TODO);
        }

        findTodo.get().updateContent(request.getContent());
        return todolist.stream().map(TodoDto::from).toList();
    }

    public void deleteTodo(String email, int todoId) {
        existsMemberByEmail(email);

        List<Todo> todolist = todoRepository.searchAllTodoByToday(email);
        if (todolist.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_TODO);
        }

        Optional<Todo> findTodo = todolist.stream()
                .filter(todo -> todo.getId().equals((long) todoId))
                .findFirst();
        if (findTodo.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_TODO);
        }

        todoRepository.delete(findTodo.get());
        for (int i = 0; i < todolist.size(); i++) {
            todolist.get(i).updateOrder(i);
        }
    }

    @Cacheable(value = "todolist", key = "'user:'+#email")
    public List<TodoDto> getAllTodoCompleted(String email) {
        existsMemberByEmail(email);
        List<Todo> completedTodolist = todoRepository.searchAllTodoCompleted(email);
        return completedTodolist.stream().map(TodoDto::from).toList();
    }

    @Transactional
    public List<TodoDto> updateTodoOrder(String email, Long draggedId, Long targetId) {
        existsMemberByEmail(email);
        List<Todo> todolist = todoRepository.searchAllTodoByToday(email);
        if (todolist.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_TODO);
        }

        Todo draggedTodo = null, targetTodo = null;
        for (Todo todo : todolist) {
            if (todo.getId().equals(draggedId)) {
                draggedTodo = todo;
            }
            if (todo.getId().equals(targetId)) {
                targetTodo = todo;
            }
        }
        if (draggedTodo == null || targetTodo == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_TODO);
        }

        int draggedOrder = draggedTodo.getTodoOrder();
        int targetOrder = targetTodo.getTodoOrder();

        if (Math.abs(draggedOrder - targetOrder) == 1) {
            draggedTodo.updateOrder(targetOrder);
            targetTodo.updateOrder(draggedOrder);
        } else if (targetOrder > draggedOrder) {
            for (int i = draggedOrder; i < targetOrder; i++) {
                if (todolist.get(i).getId().equals(draggedId)) {
                    todolist.get(i).updateOrder(targetOrder - 1);
                    continue;
                }
                todolist.get(i).updateOrder(todolist.get(i).getTodoOrder() - 1);
            }
        } else {
            for (int i = targetOrder; i <= draggedOrder; i++) {
                if (todolist.get(i).getId().equals(draggedId)) {
                    todolist.get(i).updateOrder(targetOrder);
                    continue;
                }
                todolist.get(i).updateOrder(todolist.get(i).getTodoOrder() + 1);
            }
        }

        Collections.sort(todolist);
        return todolist.stream().map(TodoDto::from).toList();
    }

    private int findMaxByMemberToOrder(List<Todo> todoList) {
        Optional<Todo> orderMaxTodo = todoList.stream()
                .max(Comparator.comparingInt(Todo::getTodoOrder));

        return orderMaxTodo.map(Todo::getTodoOrder).orElse(0);
    }

    private void existsMemberByEmail(String email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }
    }
}
