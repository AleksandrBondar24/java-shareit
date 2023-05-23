package ru.practicum.shareit.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequest;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplIntegrationTest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private ItemRequestService itemRequestService;
    private ItemRequestDto itemRequestDto;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    public void createEnvironment() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        user = new User();
        user.setName("Серж");
        user.setEmail("1234@mail.ru");
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Новая дрель");
        Item item = new Item();
        item.setOwner(user);
        item.setIsAvailable(Boolean.TRUE);
        item.setName("Дрель");
        item.setDescription("Новая");
        userRepository.save(user);
        itemRequest = toItemRequest(itemRequestDto, user);
        var itemRequestResult = itemRequestRepository.save(itemRequest);
        item.setRequest(itemRequestResult);
        itemRepository.save(item);
    }

    @Test
    void saveItemRequest() {
        var result = itemRequestService.create(user.getId(), itemRequestDto);

        assertThat(result, notNullValue());
        assertThat(result.getDescription(), equalTo("Новая дрель"));
    }

    @Test
    void getAllItemRequestByOwner() {
        var result = itemRequestService.findAllByOwner(user.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void getAllItemRequest() {
        var userNew = new User();
        userNew.setName("Alex");
        userNew.setEmail("4321@mail.ru");
        var userResult = userRepository.save(userNew);
        var result = itemRequestService.findAll(userResult.getId(), PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void getItemRequestByRequestId() {
        var result = itemRequestService.findByRequestId(user.getId(), itemRequest.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(itemRequest.getId()));
        assertThat(result.getItems().size(), equalTo(1));
    }
}
