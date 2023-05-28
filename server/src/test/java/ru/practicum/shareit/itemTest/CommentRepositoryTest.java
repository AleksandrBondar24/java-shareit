package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    private Item item;

    @BeforeEach
    public void createEnvironment() {
        User user = new User();
        user.setName("Серж");
        user.setEmail("12345.@mail.ru");
        userRepository.save(user);
        item = new Item();
        item.setName("Дрель");
        item.setDescription("Новая");
        item.setIsAvailable(Boolean.TRUE);
        item.setOwner(user);
        itemRepository.save(item);
        Comment comment = new Comment();
        comment.setText("Норм");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        commentRepository.save(comment);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyFindAllByItemId() {
        var result = commentRepository.findAllByItemId(item.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }
}
