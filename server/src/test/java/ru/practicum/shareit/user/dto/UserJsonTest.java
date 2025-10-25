package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userCreateDto_Serialization_ShouldWork() throws Exception {
        UserCreateDto userDto = new UserCreateDto("John", "john@mail.com");

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains("\"name\":\"John\"");
        assertThat(json).contains("\"email\":\"john@mail.com\"");
    }

    @Test
    void userDto_Deserialization_ShouldWork() throws Exception {
        String json = "{\"id\":1,\"name\":\"John\",\"email\":\"john@mail.com\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("John");
        assertThat(userDto.getEmail()).isEqualTo("john@mail.com");
    }
}