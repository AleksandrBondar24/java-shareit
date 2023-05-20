package ru.practicum.shareit.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    @Mock
    private final ItemRequestRepository itemRequestRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final ItemRepository itemRepository;
    private ItemRequestService itemRequestService;
    private ItemRequestDto itemRequestDto;
    private User user;

    @BeforeEach
    public void createEnvironment() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        user = new User();
        user.setId(1L);
        user.setName("Серж");
        user.setEmail("1234@mail.ru");
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Новая дрель");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when((userRepository.existsById(anyLong())))
                .thenReturn(Boolean.valueOf("true"));
    }

    @Test
    void saveItemRequest() {
        var result = itemRequestService.create(1L, itemRequestDto);

        assertThat(result, notNullValue());
        assertThat(result.getDescription(), equalTo(itemRequestDto.getDescription()));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void getAllItemRequestByOwner() {
        when(itemRequestRepository.findAllByRequestor_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        var result = itemRequestService.findAllByOwner(1L);

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(itemRequestRepository, times(1)).findAllByRequestor_Id(anyLong(), any());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void getAllItemRequest() {
        when(itemRequestRepository.findAllByRequestor_IdNot(anyLong(), any()))
                .thenReturn(Page.empty());

        var result = itemRequestService.findAll(1L, PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(itemRequestRepository, times(1)).findAllByRequestor_IdNot(anyLong(), any());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void getItemRequestByRequestId() {
        var itemRequest = toItemRequest(itemRequestDto, user);

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(Collections.emptyList());

        var result = itemRequestService.findByRequestId(1L, 1L);

        assertThat(result, notNullValue());
        verify(itemRequestRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findAllByRequestId(1L);
        verify(userRepository, times(1)).existsById(1L);
    }
}
