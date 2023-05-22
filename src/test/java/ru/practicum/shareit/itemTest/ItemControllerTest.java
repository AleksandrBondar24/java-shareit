package ru.practicum.shareit.itemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;
    private final Long USERID = 1L;
    private final int FROM = 0;
    private final int SIZE = 10;

    private final CommentDto commentDto = new CommentDto(
            "Отл"
    );
    private final CommentResponseDto commentResponseDto = new CommentResponseDto(
            1L,
            "Отл",
            "Серж",
            null
    );
    private final ItemDto itemDto = new ItemDto(
            1L,
            "Дрель",
            "Новая",
            Boolean.TRUE,
            null,
            null
    );
    private final ItemResponseDto itemResponseDto = new ItemResponseDto(
            1L,
            "Дрель",
            "Новая",
            Boolean.TRUE,
            null,
            null,
            null,
            null,
            null
    );

    @Test
    void createItem() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
        verify(itemService, times(1)).create(anyLong(), any());
    }

    @Test
    void updateItem() throws Exception {
        itemDto.setName("Пила");
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/" + USERID)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
        verify(itemService, times(1)).update(anyLong(), anyLong(), any());
    }

    @Test
    void getItem() throws Exception {
        when(itemService.findById(anyLong(), anyLong()))
                .thenReturn(itemResponseDto);

        mvc.perform(get("/items/" + itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()));
        verify(itemService, times(1)).findById(anyLong(), anyLong());
    }

    @Test
    void getAllItems() throws Exception {
        final var page = PageRequest.of(FROM > 0 ? FROM / SIZE : 0, SIZE);

        when(itemService.findAll(USERID, page))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(FROM))
                        .param("size", String.valueOf(SIZE))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1)).findAll(USERID, page);
    }

    @Test
    void searchItems() throws Exception {
        var text = "Дрель";
        final var page = PageRequest.of(FROM > 0 ? FROM / SIZE : 0, SIZE);

        when(itemService.searchItems(USERID, text, page))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", text)
                        .param("from", String.valueOf(FROM))
                        .param("size", String.valueOf(SIZE))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1)).searchItems(USERID, text, page);
    }

    @Test
    void createComment() throws Exception {
        var itemId = 1L;
        when(itemService.createComment(USERID, commentDto, itemId))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()));
        verify(itemService, times(1)).createComment(USERID, commentDto, itemId);
    }
}
