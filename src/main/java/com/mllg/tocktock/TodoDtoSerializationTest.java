package com.mllg.tocktock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mllg.tocktock.domain.responseDto.TodoDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TodoDtoSerializationTest {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO 8601 포맷으로 출력
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // 보기 좋게 들여쓰기

        TodoDto todo1 = new TodoDto(1L, false, "테스트 할일 1", 1, LocalDateTime.now(), LocalDateTime.now().minusHours(1));
        TodoDto todo2 = new TodoDto(2L, true, "테스트 할일 2 (완료)", 2, LocalDateTime.now(), LocalDateTime.now().minusDays(1));
        List<TodoDto> todoList = Arrays.asList(todo1, todo2);

        try {
            String jsonString = objectMapper.writeValueAsString(todoList);
            System.out.println("직렬화 성공:\n" + jsonString);

            List<TodoDto> deserializedTodoList = objectMapper.readValue(jsonString,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TodoDto.class));

            System.out.println("역직렬화 성공 (첫 번째 할일 내용): " + deserializedTodoList.get(0).getContent());

        } catch (Exception e) {
            System.err.println("직렬화/역직렬화 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 스택 트레이스를 꼭 확인하세요!
        }
    }
}
