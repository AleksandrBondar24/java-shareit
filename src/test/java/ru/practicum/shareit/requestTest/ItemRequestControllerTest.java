package ru.practicum.shareit.requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;
    private final Long USERID = 1L;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            null,
            "Новая дрель",
            null,
            null
    );
    private final ItemRequestDto itemRequestDtoResponse = new ItemRequestDto(
            1L,
            "Новая дрель",
            LocalDateTime.now(),
            Collections.emptyList()
    );

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.create(anyLong(), any()))
                .thenReturn(itemRequestDtoResponse);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoResponse.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoResponse.getDescription()));
        verify(itemRequestService, times(1)).create(anyLong(), any());
    }

    @Test
    void getAllByOwner() throws Exception {
        when(itemRequestService.findAllByOwner(USERID))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).findAllByOwner(USERID);
    }

    @Test
    void getFindAll() throws Exception {
        var from = 0;
        var size = 10;
        final Sort sort = Sort.by("created").descending();
        final var page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        when(itemRequestService.findAll(USERID, page))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).findAll(USERID, page);
    }

    @Test
    void getFindByRequestId() throws Exception {
        var requestId = 1L;
        when(itemRequestService.findByRequestId(USERID, requestId))
                .thenReturn(itemRequestDtoResponse);

        mvc.perform(get("/requests/" + requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoResponse.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoResponse.getDescription()));
        verify(itemRequestService, times(1)).findByRequestId(USERID, requestId);
    }
}
