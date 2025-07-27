package com.mllg.tocktock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mllg.tocktock.domain.requestDto.TodoAddRequest;
import com.mllg.tocktock.domain.requestDto.TodoUpdateCheckboxRequest;
import com.mllg.tocktock.domain.requestDto.TodoUpdateContentRequest;
import com.mllg.tocktock.domain.requestDto.TodoUpdateOrderRequest;
import com.mllg.tocktock.entity.Member;
import com.mllg.tocktock.entity.Todo;
import com.mllg.tocktock.repository.MemberRepository;
import com.mllg.tocktock.repository.TodoRepository;
import com.mllg.tocktock.type.MemberType;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("TodoController 통합 테스트")
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    private final String name = "integration_user";
    private final String email = "integration@email.com";
    private final String picture = "https://integration.com/integration.jpg";

    private DefaultOAuth2User createMockOAuth2User() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", name);
        attributes.put("email", email);
        attributes.put("picture", picture);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "name"
        );
    }

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("ALTER TABLE todo AUTO_INCREMENT = 1").executeUpdate();
        Member member = memberRepository.save(Member.builder()
                .email(email)
                .name(name)
                .provider("google")
                .todoList(new ArrayList<>())
                .oauth2Id("12305139827")
                .role(MemberType.USER)
                .build());

        todoRepository.saveAll(List.of(
                        Todo.builder()
                                .isDone(false)
                                .content("content 1")
                                .member(member)
                                .todoOrder(0)
                                .completedAt(null)
                                .build(),
                        Todo.builder()
                                .isDone(false)
                                .content("content 2")
                                .member(member)
                                .todoOrder(1)
                                .completedAt(null)
                                .build(),
                        Todo.builder()
                                .isDone(false)
                                .content("content 3")
                                .member(member)
                                .todoOrder(2)
                                .completedAt(null)
                                .build(),
                        Todo.builder()
                                .isDone(true)
                                .content("content 4")
                                .member(member)
                                .todoOrder(3)
                                .completedAt(LocalDateTime.now().minusDays(1))
                                .build(),
                        Todo.builder()
                                .isDone(false)
                                .content("content 5")
                                .member(member)
                                .todoOrder(4)
                                .completedAt(null)
                                .build()
                )
        );

    }

    @Test
    @DisplayName("할 일 목록 조회 성공")
    void getAllTodoSuccess() throws Exception {
        //given
        //when & then
        mockMvc.perform(get("/todolist")
                        .with(oauth2Login().oauth2User(createMockOAuth2User())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$[0].content").value("content 1"))
                .andExpect(jsonPath("$[1].content").value("content 2"))
                .andExpect(jsonPath("$[2].content").value("content 3"))
                .andExpect(jsonPath("$[3].content").value("content 5"));
    }

    @Test
    @DisplayName("할 일 목록 조회 실패 - 로그인 인증이 되지 않은 요청")
    void getAllTodoFailure() throws Exception {
        //given
        //when & then
        mockMvc.perform(get("/todolist"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Todo 추가 성공")
    void addTodoSuccess() throws Exception {
        //given
        TodoAddRequest request = TodoAddRequest.builder()
                .content("added content")
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(post("/todolist")
                        .with(oauth2Login().oauth2User(createMockOAuth2User()))
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("added content"))
                .andExpect(jsonPath("$.id").value(6));
    }

    @Test
    @DisplayName("Todo 체크박스 업데이트 성공")
    void updateCheckboxTodoSuccess() throws Exception {
        //given
        TodoUpdateCheckboxRequest request = TodoUpdateCheckboxRequest.builder()
                .id(1L)
                .isDone(true)
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(patch("/todolist/checkbox")
                        .with(oauth2Login().oauth2User(createMockOAuth2User()))
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[2].id").value(5))
                .andExpect(jsonPath("$[3].id").value(1));
    }

    @Test
    @DisplayName("Todo 내용 업데이트 성공")
    void updateContentTodoSuccess() throws Exception {
        //given
        TodoUpdateContentRequest request = TodoUpdateContentRequest.builder()
                .id(1L)
                .content("updated content")
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(patch("/todolist/content")
                        .with(oauth2Login().oauth2User(createMockOAuth2User()))
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("updated content"));
    }

    @Test
    @DisplayName("Todo 삭제 성공")
    void deleteTodo() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(delete("/todolist/1")
                        .with(oauth2Login().oauth2User(createMockOAuth2User()))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(todoRepository.findById(1L)).isEmpty();
    }

    @Test
    @DisplayName("Todo 완료목록 조회 성공")
    void getAllTodoCompleteSuccess() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/todolist/complete")
                        .with(oauth2Login().oauth2User(createMockOAuth2User()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.todoList[0].isDone").value(true))
                .andExpect(jsonPath("$.todoList[0].content").value("content 4"))
        ;

    }

    @Test
    @DisplayName("Todo 순서 업데이트 성공 - 1 > 3")
    void updateTodoOrderSuccess1() throws Exception {
        //given
        TodoUpdateOrderRequest request = TodoUpdateOrderRequest.builder()
                .draggedId(1L)
                .targetId(3L)
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(patch("/todolist/order")
                        .with(oauth2Login().oauth2User(createMockOAuth2User()))
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[2].id").value(1))
                .andExpect(jsonPath("$[3].id").value(5))
        ;
    }

    @Test
    @DisplayName("Todo 순서 업데이트 성공 - 3 > 1")
    void updateTodoOrderSuccess2() throws Exception {
        //given
        TodoUpdateOrderRequest request = TodoUpdateOrderRequest.builder()
                .draggedId(3L)
                .targetId(1L)
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(patch("/todolist/order")
                        .with(oauth2Login().oauth2User(createMockOAuth2User()))
                        .with(csrf())
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[1].id").value(1))
                .andExpect(jsonPath("$[2].id").value(2))
                .andExpect(jsonPath("$[3].id").value(5))
        ;
    }

}