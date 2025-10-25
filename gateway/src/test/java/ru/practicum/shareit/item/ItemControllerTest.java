package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void createItem_WithValidData_ShouldReturnOk() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("Drill", "Powerful drill", true, null);

        when(itemClient.createItem(any(ItemCreateDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_WithBlankName_ShouldReturnBadRequest() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("", "Description", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_WithBlankDescription_ShouldReturnBadRequest() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("Name", "", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_WithNullAvailable_ShouldReturnBadRequest() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("Name", "Description", null, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_WithValidData_ShouldReturnOk() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto("Updated Drill", "Updated description", null);

        when(itemClient.updateItem(anyLong(), any(ItemUpdateDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_ShouldReturnOk() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByOwner_ShouldReturnOk() throws Exception {
        when(itemClient.getItemsByOwner(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_ShouldReturnOk() throws Exception {
        when(itemClient.searchItems(anyString()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_WithValidData_ShouldReturnOk() throws Exception {
        CommentCreateDto commentDto = new CommentCreateDto("Great item!");

        when(itemClient.addComment(anyLong(), any(CommentCreateDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_WithBlankText_ShouldReturnBadRequest() throws Exception {
        CommentCreateDto commentDto = new CommentCreateDto("");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}