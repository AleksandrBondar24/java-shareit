package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final CommentRepository commentRepository;
    @Mock
    private final ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private User user;

    @BeforeEach
    public void createEnvironment() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
        user = new User();
        user.setId(1L);
        user.setName("Серж");
        user.setEmail("12345@mail.ru");

        when(itemRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void saveItem() {
        var userId = 1L;
        var user = new User();
        user.setId(2L);

        var itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequestor(user);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        var itemDto = new ItemDto();
        itemDto.setRequestId(itemRequest.getId());

        var result = itemService.create(userId, itemDto);

        assertThat(result, notNullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateItem() {
        var item = new Item();
        item.setId(1L);
        item.setOwner(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        var itemDto = new ItemDto();

        var result = itemService.update(user.getId(), item.getId(), itemDto);

        assertThat(result, notNullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemById() {
        var item = new Item();
        item.setId(1L);
        item.setOwner(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        var booker = new User();
        booker.setId(88L);

        var booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        when(bookingRepository.findByItem_IdAndStatusIs(anyLong(), any()))
                .thenReturn(List.of(booking));

        var comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        comment.setAuthor(booker);
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        var result = itemService.findById(item.getId(), user.getId());

        assertThat(result, notNullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
        verify(bookingRepository, times(1)).findByItem_IdAndStatusIs(anyLong(), any());
    }

    @Test
    void getAllItem() {
        var item = new Item();
        item.setId(1L);
        item.setOwner(user);
        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(Page.empty());

        var booker = new User();
        booker.setId(88L);

        var booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        when(bookingRepository.findAllByItem_IdInAndStatusIs(anyList(), any()))
                .thenReturn(Collections.emptyList());

        var result = itemService.findAll(user.getId(), PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStatusIs(anyList(), any());
    }

    @Test
    void searchItems() {
        when(itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(
                anyString(), anyString(), any()))
                .thenReturn(Page.empty());

        var result = itemService.searchItems(user.getId(), "Дрель", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).
                findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(anyString(), anyString(), any());
    }

    @Test
    void createComment() {
        var commentDto = new CommentDto();
        commentDto.setText("BLB");

        var item = new Item();
        item.setId(1L);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findByItem_IdAndEndIsBefore(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        verify(userRepository, times(0)).findById(user.getId());
        verify(itemRepository, times(0)).findById(anyLong());
        verify(bookingRepository, times(0)).findByItem_IdAndEndIsBefore(anyLong(), any());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.createComment(user.getId(), commentDto, 1L));
    }
}
