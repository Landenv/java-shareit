package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void itemCreateDto_Serialization_ShouldWork() throws Exception {
        ItemCreateDto itemDto = new ItemCreateDto("Drill", "Powerful", true, null);

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"name\":\"Drill\"");
        assertThat(json).contains("\"description\":\"Powerful\"");
        assertThat(json).contains("\"available\":true");
    }

    @Test
    void itemCreateDto_Deserialization_ShouldWork() throws Exception {
        String json = "{\"name\":\"Drill\",\"description\":\"Powerful\",\"available\":true,\"requestId\":null}";

        ItemCreateDto itemDto = objectMapper.readValue(json, ItemCreateDto.class);

        assertThat(itemDto.getName()).isEqualTo("Drill");
        assertThat(itemDto.getDescription()).isEqualTo("Powerful");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void itemDto_Serialization_ShouldWork() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful", true, 1L, null, null, null, null);

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Drill\"");
        assertThat(json).contains("\"available\":true");
    }

    @Test
    void commentDto_Serialization_ShouldWork() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Great item!", "John", LocalDateTime.now());

        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Great item!\"");
        assertThat(json).contains("\"authorName\":\"John\"");
    }
}