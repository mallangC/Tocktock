package com.mllg.tocktock.controller;

import com.mllg.tocktock.domain.requestDto.TodoAddRequest;
import com.mllg.tocktock.domain.requestDto.TodoUpdateCheckboxRequest;
import com.mllg.tocktock.domain.requestDto.TodoUpdateContentRequest;
import com.mllg.tocktock.domain.responseDto.TodoDto;
import com.mllg.tocktock.domain.responseDto.TodolistDto;
import com.mllg.tocktock.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/todolist")
@RestController
public class TodoController {

    final private TodoService todoService;

    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodo(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            throw new RuntimeException("OAuth2User is null");
        }
        String email = oauth2User.getAttribute("email");
        return ResponseEntity.ok(todoService.getAllTodo(email));
    }

    @PostMapping
    public ResponseEntity<TodoDto> addTodo(@AuthenticationPrincipal OAuth2User oauth2User,
                                           @RequestBody @Valid TodoAddRequest request) {
        if (oauth2User == null) {
            throw new RuntimeException("OAuth2User is null");
        }
        String email = oauth2User.getAttribute("email");
        return ResponseEntity.ok(todoService.addTodo(email, request));
    }

    @PatchMapping("/checkbox")
    public ResponseEntity<List<TodoDto>> updateCheckboxTodo(@AuthenticationPrincipal OAuth2User oauth2User,
                                                            @RequestBody @Valid TodoUpdateCheckboxRequest request) {
        if (oauth2User == null) {
            throw new RuntimeException("OAuth2User is null");
        }
        String email = oauth2User.getAttribute("email");
        return ResponseEntity.ok(todoService.updateTodoCheckbox(email, request));
    }

    @PatchMapping("/content")
    public ResponseEntity<List<TodoDto>> updateContentTodo(@AuthenticationPrincipal OAuth2User oauth2User,
                                                           @RequestBody @Valid TodoUpdateContentRequest request) {
        if (oauth2User == null) {
            throw new RuntimeException("OAuth2User is null");
        }
        String email = oauth2User.getAttribute("email");
        return ResponseEntity.ok(todoService.updateTodoContent(email, request));
    }

    @DeleteMapping("/{todo_id}")
    public ResponseEntity<String> deleteTodo(@AuthenticationPrincipal OAuth2User oauth2User,
                                                    @PathVariable("todo_id") int todoId) {
        if (oauth2User == null) {
            throw new RuntimeException("OAuth2User is null");
        }
        String email = oauth2User.getAttribute("email");
        todoService.deleteTodo(email, todoId);
        return ResponseEntity.ok("할 일 삭제 성공");
    }


    @GetMapping("/complete")
    public ResponseEntity<TodolistDto> getAllTodoComplete(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            throw new RuntimeException("OAuth2User is null");
        }
        String email = oauth2User.getAttribute("email");
        List<TodoDto> todoList = todoService.getAllTodoCompleted(email);
        TodolistDto todolistDto = TodolistDto.from(todoList);
        return ResponseEntity.ok(todolistDto);
    }

}
