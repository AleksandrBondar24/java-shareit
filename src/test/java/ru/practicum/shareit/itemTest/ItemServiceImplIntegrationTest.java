package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.shareit.item.ItemMapper.toItem;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private User user;
    private User owner;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    public void createEnvironment() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
        user = new User();
        user.setName("Серж");
        user.setEmail("12345@mail.ru");
        owner = new User();
        owner.setName("Женя");
        owner.setEmail("54321@mail.ru");
        userRepository.save(user);
        userRepository.save(owner);
        itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Новая дрель");
        itemRequestRepository.save(itemRequest);
        itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Новая");
        itemDto.setAvailable(Boolean.TRUE);
        itemDto.setRequestId(itemRequest.getId());
        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        comment = new Comment();
        comment.setText("Норм");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void saveItem() {
        var result = itemService.create(owner.getId(), itemDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(itemDto.getName()));
        assertThat(result.getRequestId(), equalTo(itemRequest.getId()));
    }

    @Test
    void updateItem() {
        final var itemId = itemService.create(owner.getId(), itemDto).getId();
        itemDto.setName("Пила");
        itemDto.setDescription("Старая");
        var result = itemService.update(owner.getId(), itemId, itemDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(itemDto.getName()));
        assertThat(result.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void getItemById() {
        var itemResult = itemService.create(owner.getId(), itemDto);
        var item = toItem(itemResult);
        item.setId(itemResult.getId());
        booking.setItem(item);
        comment.setItem(item);
        bookingRepository.save(booking);
        commentRepository.save(comment);

        var result = itemService.findById(item.getId(), owner.getId());

        assertThat(result, notNullValue());
        assertThat(result.getComments().size(), equalTo(1));
        assertThat(result.getNextBooking().getBookerId(), equalTo(booking.getBooker().getId()));
    }

    @Test
    void getAllItem() {
        var itemResult = itemService.create(owner.getId(), itemDto);
        var item = toItem(itemResult);
        item.setId(itemResult.getId());
        booking.setItem(item);
        bookingRepository.save(booking);

        var result = itemService.findAll(owner.getId(), PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void searchItems() {
        itemService.create(owner.getId(), itemDto);
        var text = "Дрель";
        var result = itemService.searchItems(user.getId(), text, PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void createComment() {
        var itemResult = itemService.create(owner.getId(), itemDto);
        var item = toItem(itemResult);
        item.setId(itemResult.getId());
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(4));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking);
        var commendDto = new CommentDto();
        commendDto.setText("Отл");

        var result = itemService.createComment(user.getId(), commendDto, item.getId());

        assertThat(result, notNullValue());
        assertThat(result.getAuthorName(), equalTo(user.getName()));
    }
}
