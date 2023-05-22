package ru.practicum.shareit.bookingTest;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.NotFoundException;


@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;
    private final Long USER_ID = 1L;
    private final int FROM = 0;
    private final int SIZE = 10;

    private final BookingRequestDto bookingRequestDto = new BookingRequestDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
    );
    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            new BookingDto.Item(1L, "Дрель"),
            new BookingDto.Booker(1L, "Серж"),
            BookingStatus.WAITING
    );

    @Test
    void createBooking() throws Exception {
        when(bookingService.create(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()));
        verify(bookingService, times(1)).create(any(), anyLong());
    }

    @Test
    void updateBooking() throws Exception {
        boolean approved = Boolean.TRUE;
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.update(bookingDto.getId(), USER_ID, approved))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/" + bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()));
        verify(bookingService, times(1)).update(bookingDto.getId(), USER_ID, approved);
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.findById(USER_ID, bookingDto.getId()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/" + bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()));
        verify(bookingService, times(1)).findById(USER_ID, bookingDto.getId());
    }

    @Test
    void getBookingsByBooker() throws Exception {
        var state = "FUTURE";
        final var sort = Sort.by("start").descending();
        final var page = PageRequest.of(FROM > 0 ? FROM / SIZE : 0, SIZE, sort);

        when(bookingService.findAllByBooker(USER_ID, state, page))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", state)
                        .param("from", String.valueOf(FROM))
                        .param("size", String.valueOf(SIZE))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).findAllByBooker(USER_ID, state, page);
    }

    @Test
    void getBookingsByOwner() throws Exception {
        var state = "ALL";
        final var sort = Sort.by("start").descending();
        final var page = PageRequest.of(FROM > 0 ? FROM / SIZE : 0, SIZE, sort);

        when(bookingService.findAllByOwner(USER_ID, state, page))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", state)
                        .param("from", String.valueOf(FROM))
                        .param("size", String.valueOf(SIZE))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).findAllByOwner(USER_ID, state, page);
    }

    @Test
    void shouldReturnFailOnUnknownStatus() throws Exception {
        var state = "NEVER";
        final var sort = Sort.by("start").descending();
        var page = PageRequest.of(FROM > 0 ? FROM / SIZE : 0, SIZE, sort);

        when(bookingService.findAllByBooker(USER_ID, state, page))
                .thenThrow(new NotFoundException("Unknown state: " + state));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(FROM))
                        .param("size", String.valueOf(SIZE))
                        .param("state", state)
                        .accept(MediaType.ALL))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\"error\":\"Unknown state: " + state + "\"}"));
        verify(bookingService, times(1)).findAllByBooker(USER_ID, state, page);
    }
}

