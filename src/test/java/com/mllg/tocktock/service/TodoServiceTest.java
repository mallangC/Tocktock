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
import com.mllg.tocktock.type.MemberType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private TodoService todoService;

    Member baseMember = Member.builder()
            .id(1L)
            .email("test@email.com")
            .name("test")
            .provider("google")
            .role(MemberType.USER)
            .todoList(new ArrayList<>(List.of(Todo.builder()
                            .id(2L)
                            .todoOrder(0)
                            .content("Todo id 2 content")
                            .isDone(false)
                            .build(),
                    Todo.builder()
                            .id(1L)
                            .todoOrder(1)
                            .content("Todo id 1 content")
                            .isDone(false)
                            .build()
            )))
            .build();

    List<Todo> todos = new ArrayList<>(Arrays.asList(Todo.builder()
                    .id(1L)
                    .isDone(false)
                    .todoOrder(0)
                    .content("completed id 1")
                    .build(),
            Todo.builder()
                    .id(2L)
                    .isDone(false)
                    .todoOrder(1)
                    .content("completed id 2")
                    .build(),
            Todo.builder()
                    .id(3L)
                    .isDone(false)
                    .todoOrder(2)
                    .content("completed id 3")
                    .build(),
            Todo.builder()
                    .id(4L)
                    .isDone(false)
                    .todoOrder(3)
                    .content("completed id 4")
                    .build(),
            Todo.builder()
                    .id(5L)
                    .isDone(false)
                    .todoOrder(4)
                    .content("completed id 5")
                    .build(),
            Todo.builder()
                    .id(6L)
                    .isDone(false)
                    .todoOrder(5)
                    .content("completed id 6")
                    .build()));

    Todo baseTodo = Todo.builder()
            .id(1L)
            .member(baseMember)
            .isDone(false)
            .content("test content")
            .todoOrder(0)
            .build();

    String email = "test@email.com";

    @Test
    @DisplayName("Todo 추가 성공")
    void addTodoSuccess() {
        //given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(baseMember));
        given(todoRepository.save(any()))
                .willReturn(baseTodo);

        TodoAddRequest request = TodoAddRequest.builder()
                .content("test content")
                .build();

        //when
        TodoDto result = todoService.addTodo(email, request);
        //then
        assertEquals(result.getId(), baseTodo.getId());
        assertEquals(result.getTodoOrder(), baseTodo.getTodoOrder());
        assertEquals(result.getIsDone(), baseTodo.getIsDone());
        assertEquals(result.getContent(), baseTodo.getContent());
        verify(todoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Todo 추가 실패 - 회원을 찾을 수 없음")
    void addTodoSuccessFailure1() {
        //given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        TodoAddRequest request = TodoAddRequest.builder()
                .content("test content")
                .build();

        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> todoService.addTodo(email, request));

        //then
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
        verify(todoRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Todo 전체 조회")
    void getAllTodoSuccess() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(baseMember.getTodoList());
        //when
        List<TodoDto> todoDtoList = todoService.getAllTodo(email);

        //then
        assertEquals(2L, todoDtoList.get(0).getId());
        assertEquals(1L, todoDtoList.get(1).getId());
        assertEquals(0, todoDtoList.get(0).getTodoOrder());
        assertEquals(1, todoDtoList.get(1).getTodoOrder());
        assertEquals("Todo id 2 content", todoDtoList.get(0).getContent());
        assertEquals("Todo id 1 content", todoDtoList.get(1).getContent());

        assertEquals(2, todoDtoList.size());
    }

    @Test
    @DisplayName("Todo 체크박스 수정")
    void updateTodoCheckboxSuccess() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);

        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(baseMember.getTodoList());
        TodoUpdateCheckboxRequest request = TodoUpdateCheckboxRequest.builder()
                .id(1L)
                .isDone(true)
                .build();

        //when
        List<TodoDto> todoDtoList = todoService.updateTodoCheckbox(email, request);
        //then
        assertEquals(2L, todoDtoList.get(0).getId());
        assertEquals(0, todoDtoList.get(0).getTodoOrder());
        assertEquals(1L, todoDtoList.get(1).getId());
        assertEquals(1, todoDtoList.get(1).getTodoOrder());
    }

    @Test
    @DisplayName("Todo 체크박스 수정 실패 - 회원을 찾을 수 없음")
    void updateTodoCheckboxFailure1() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(false);

        TodoUpdateCheckboxRequest request = TodoUpdateCheckboxRequest.builder()
                .id(1L)
                .isDone(true)
                .build();
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.updateTodoCheckbox(email, request));
        //then
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 체크박스 수정 실패 - Todolist가 비어있음")
    void updateTodoCheckboxFailure2() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);

        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(new ArrayList<>());
        TodoUpdateCheckboxRequest request = TodoUpdateCheckboxRequest.builder()
                .id(1L)
                .isDone(true)
                .build();

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.updateTodoCheckbox(email, request));
        //then
        assertEquals(ErrorCode.NOT_FOUND_TODO, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 내용 수정 성공")
    void updateTodoContentSuccess() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(baseMember.getTodoList());

        String updatedContent = "updated content";

        TodoUpdateContentRequest request =
                TodoUpdateContentRequest.builder()
                        .id(1L)
                        .content(updatedContent)
                        .build();
        //when
        List<TodoDto> todoDtoList = todoService.updateTodoContent(email, request);
        //then
        assertEquals(1L, todoDtoList.get(1).getId());
        assertEquals(updatedContent, todoDtoList.get(1).getContent());
    }

    @Test
    @DisplayName("Todo 내용 수정 실패 - 회원을 찾을 수 없음")
    void updateTodoContentFailure1() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(false);

        String updatedContent = "updated content";

        TodoUpdateContentRequest request =
                TodoUpdateContentRequest.builder()
                        .id(1L)
                        .content(updatedContent)
                        .build();
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.updateTodoContent(email, request));
        //then
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 내용 수정 실패 - todolist가 비어있음")
    void updateTodoContentFailure2() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(new ArrayList<>());

        String updatedContent = "updated content";

        TodoUpdateContentRequest request =
                TodoUpdateContentRequest.builder()
                        .id(1L)
                        .content(updatedContent)
                        .build();
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.updateTodoContent(email, request));
        //then
        assertEquals(ErrorCode.NOT_FOUND_TODO, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 내용 수정 실패 - request에 맞는 id가 없음")
    void updateTodoContentFailure3() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(baseMember.getTodoList());

        String updatedContent = "updated content";

        TodoUpdateContentRequest request =
                TodoUpdateContentRequest.builder()
                        .id(3L)
                        .content(updatedContent)
                        .build();
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.updateTodoContent(email, request));
        //then
        assertEquals(ErrorCode.NOT_FOUND_TODO, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 삭제 성공")
    void deleteTodoSuccess() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(baseMember.getTodoList());
        //when
        todoService.deleteTodo(email, 1);
        //then
        verify(todoRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("Todo 삭제 실패 - 회원을 찾을 수 없음")
    void deleteTodoFailure1() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(false);
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.deleteTodo(email, 1));
        //then
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 삭제 실패 - todolist가 비어있음")
    void deleteTodoFailure2() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(new ArrayList<>());
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.deleteTodo(email, 1));
        //then
        assertEquals(ErrorCode.NOT_FOUND_TODO, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 삭제 실패 - id에 맞는 Todo를 찾을 수 없음")
    void deleteTodoFailure3() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(baseMember.getTodoList());
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.deleteTodo(email, 3));
        //then
        assertEquals(ErrorCode.NOT_FOUND_TODO, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 삭제 실패 - 오늘 이전에 완료된 할 일")
    void deleteTodoFailure4() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);

        Member member = Member.builder()
                .id(1L)
                .email("test@email.com")
                .name("test")
                .provider("google")
                .role(MemberType.USER)
                .todoList(new ArrayList<>(List.of(Todo.builder()
                                .id(2L)
                                .todoOrder(0)
                                .content("Todo id 2 content")
                                .isDone(false)
                                .build(),
                        Todo.builder()
                                .id(1L)
                                .todoOrder(1)
                                .content("Todo id 1 content")
                                .isDone(true)
                                .completedAt(LocalDateTime.now().minusDays(1))
                                .build()
                )))
                .build();
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(member.getTodoList());
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.deleteTodo(email, 1));
        //then
        assertEquals(ErrorCode.CANNOT_DELETE_TODO_COMPLETED_BEFORE_TODAY, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 완료목록 조회 성공")
    void getAllTodoCompleteSuccess() {
        //given
        List<Todo> todoList = List.of(Todo.builder()
                        .id(1L)
                        .content("completed id 1")
                        .todoOrder(0)
                        .isDone(true)
                        .build(),
                Todo.builder()
                        .id(2L)
                        .content("completed id 2")
                        .todoOrder(0)
                        .isDone(true)
                        .build());

        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoCompleted(anyString()))
                .willReturn(todoList);
        //when
        List<TodoDto> todoDtoList = todoService.getAllTodoCompleted(email);
        //then
        assertEquals(2, todoDtoList.size());
        assertEquals(1L, todoDtoList.get(0).getId());
        verify(todoRepository, times(1)).searchAllTodoCompleted(any());
    }

    @Test
    @DisplayName("Todo 완료목록 조회 실패 - 회원을 찾을 수 없음")
    void getAllTodoCompleteFailure() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(false);
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.getAllTodoCompleted(email));
        //then
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 순서 변경 성공 - 1 -> 4")
    void updateTodoOrderSuccess1() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(todos);
        //when
        List<TodoDto> todoList = todoService.updateTodoOrder(email, 1L, 4L); //order 0, 3
        //then
        assertEquals(6, todoList.size());
        assertEquals(2L, todoList.get(0).getId());
        assertEquals(1L, todoList.get(3).getId());
    }


    @Test
    @DisplayName("Todo 순서 변경 성공 - 4 -> 1")
    void updateTodoOrderSuccess2() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(todos);
        //when
        List<TodoDto> todoList = todoService.updateTodoOrder(email, 4L, 1L); //order 3, 0
        //then
        assertEquals(6, todoList.size());
        assertEquals(4L, todoList.get(0).getId());
        assertEquals(1L, todoList.get(1).getId());
        assertEquals(5L, todoList.get(4).getId());
    }

    @Test
    @DisplayName("Todo 순서 변경 실패 - 회원을 찾을 수 없음")
    void updateTodoOrderFailure1() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(false);
        //when

        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.updateTodoOrder(email, 4L, 1L));
        //then
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 순서 변경 실패 - 빈 Todolist")
    void updateTodoOrderFailure2() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(new ArrayList<>());
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.updateTodoOrder(email, 4L, 1L));
        //then
        assertEquals(ErrorCode.NOT_FOUND_TODO, exception.getErrorCode());
    }

    @Test
    @DisplayName("Todo 순서 변경 실패 - id에 맞는 Todo를 찾을 수 없음")
    void updateTodoOrderFailure3() {
        //given
        given(memberRepository.existsByEmail(anyString()))
                .willReturn(true);
        given(todoRepository.searchAllTodoByToday(anyString()))
                .willReturn(todos);
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                todoService.updateTodoOrder(email, 7L, 8L));
        //then
        assertEquals(ErrorCode.NOT_FOUND_TODO, exception.getErrorCode());

    }
}