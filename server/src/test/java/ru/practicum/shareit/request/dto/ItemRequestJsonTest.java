package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void itemRequestCreateDto_Serialization_ShouldWork() throws Exception {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a drill");

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"description\":\"Need a drill\"");
    }

    @Test
    void itemRequestResponseDto_Deserialization_ShouldWork() throws Exception {
        String json = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2023-01-01T10:00:00\"}";

        ItemRequestResponseDto requestDto = objectMapper.readValue(json, ItemRequestResponseDto.class);

        assertThat(requestDto.getId()).isEqualTo(1L);
        assertThat(requestDto.getDescription()).isEqualTo("Need a drill");
        assertThat(requestDto.getCreated()).isNotNull();
    }
}