package com.mllg.tocktock.service;

import com.mllg.tocktock.domain.requestDto.TodoAddRequest;
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

import java.util.ArrayList;
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
            .todoList(new ArrayList<>(List.of(
                    Todo.builder()
                            .id(1L)
                            .todoOrder(1)
                            .content("Todo id 1 content")
                            .isDone(false)
                            .build(),
                    Todo.builder()
                            .id(2L)
                            .todoOrder(0)
                            .content("Todo id 2 content")
                            .isDone(false)
                            .build()
            )))
            .build();

    Todo baseTodo = Todo.builder()
            .id(1L)
            .member(baseMember)
            .isDone(false)
            .content("test content")
            .todoOrder(0)
            .build();

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
        TodoDto result = todoService.addTodo("test@mail.com", request);
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
                () -> todoService.addTodo("test@mail.com", request));

        //then
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
        verify(todoRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Todo 전체 조회")
    void getAllTodoSuccess() {
        //given

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(baseMember));
        //when
        List<TodoDto> todoDtoList = todoService.getAllTodo("test@gmail.com");

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
    @DisplayName("Todo 체크박스 ")
    void updateTodoCheckboxSuccess () {
      //given
      //when
      //then
    }

}