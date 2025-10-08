package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ExtendedItemService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5433/share_it_test")
@ActiveProfiles("test")
public class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ExtendedItemService service;
    private final UserService userService;
    private final BookingService bookingService;

    private UserDto preCreateUser() {
        NewUserRequest newUser = new NewUserRequest("testName", "testEmail@mail.com");
        return userService.createUser(newUser);
    }

    @AfterEach
    public void cleanUp() {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        em.createQuery("DELETE FROM Comment").executeUpdate();
        em.createQuery("DELETE FROM Booking").executeUpdate();
        em.createQuery("DELETE FROM Item").executeUpdate();
        em.createQuery("DELETE FROM ItemRequest").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();

        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

        em.flush();
    }

    @Test
    public void testCreateItem() {
        UserDto user = preCreateUser();

        NewItemRequest itemRequest = new NewItemRequest(
                1L,
                "itemName",
                "testDescription",
                true,
                user.getId(),
                null);
        service.createItem(user.getId(), itemRequest);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemRequest.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemRequest.getName()));
        assertThat(item.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(itemRequest.getOwner()));
    }

    @Test
    public void testUpdateItem() {
        UserDto user = preCreateUser();

        NewItemRequest itemRequest = new NewItemRequest(
                1L,
                "itemName",
                "testDescription",
                true,
                user.getId(),
                null);
        ItemDto createdItem = service.createItem(user.getId(), itemRequest);

        UpdateItemRequest updateItemRequest = new UpdateItemRequest(
                createdItem.getId(),
                "updatedItemName",
                "updatedTestDescription",
                false,
                user.getId(),
                null);
        service.updateItem(createdItem.getId(), updateItemRequest, user.getId());

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", updateItemRequest.getName())
                .getSingleResult();

        assertThat(item.getId(), equalTo(updateItemRequest.getId()));
        assertThat(item.getName(), equalTo(updateItemRequest.getName()));
        assertThat(item.getDescription(), equalTo(updateItemRequest.getDescription()));
        assertThat(item.getAvailable(), equalTo(updateItemRequest.getAvailable()));
    }

    @Test
    public void testDeleteItem() {
        UserDto user = preCreateUser();

        NewItemRequest itemRequest = new NewItemRequest(
                1L,
                "itemName",
                "testDescription",
                true,
                user.getId(),
                null);
        ItemDto newItem = service.createItem(user.getId(), itemRequest);
        service.deleteItem(newItem.getId());

        assertThrows(NotFoundException.class, () -> service.getItemById(newItem.getId(), user.getId()));
    }

    @Test
    public void testGetItemById() {
        UserDto user = preCreateUser();

        NewItemRequest itemRequest = new NewItemRequest(
                1L,
                "itemName",
                "testDescription",
                true,
                user.getId(),
                null);
        ItemDto newItem = service.createItem(user.getId(), itemRequest);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", 1L)
                .getSingleResult();

        assertThat(item.getId(), equalTo(newItem.getId()));
        assertThat(item.getName(), equalTo(itemRequest.getName()));
        assertThat(item.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(itemRequest.getOwner()));
    }

    @Test
    public void testGetItemByText() {
        UserDto user = preCreateUser();

        NewItemRequest itemRequest = new NewItemRequest(
                1L,
                "itemName",
                "testDescription",
                true,
                user.getId(),
                null);
        service.createItem(user.getId(), itemRequest);

        Collection<ItemDto> items = service.getItemByText("desc");
        assertThat(items.stream().toList().getFirst().getDescription(), equalTo(itemRequest.getDescription()));
    }

    @Test
    public void testGetAllItemsById() {
        UserDto user = preCreateUser();

        NewItemRequest itemRequest = new NewItemRequest(
                1L,
                "itemName",
                "testDescription",
                true,
                user.getId(),
                null);
        ItemDto dto1 = service.createItem(user.getId(), itemRequest);

        NewItemRequest itemRequest2 = new NewItemRequest(
                2L,
                "itemName2",
                "testDescription2",
                true,
                user.getId(),
                null);
        ItemDto dto2 = service.createItem(user.getId(), itemRequest2);

        List<NewItemRequest> itemRequests = List.of(itemRequest, itemRequest2);

        Collection<ExtendedItemDto> items = service.getAllItemsById(user.getId());

        assertThat(itemRequests, hasSize(items.size()));
        assertThat(items, hasItem(allOf(
                hasProperty("name", equalTo("itemName")),
                hasProperty("description", equalTo("testDescription"))
        )));

        assertThat(items, hasItem(allOf(
                hasProperty("name", equalTo("itemName2")),
                hasProperty("description", equalTo("testDescription2"))
        )));
    }

    @Test
    public void testAddComment() {
        UserDto user = preCreateUser();

        NewUserRequest newUser = new NewUserRequest("testName2", "testEmail2@mail.com");
        UserDto userToComment = userService.createUser(newUser);

        NewItemRequest itemRequest = new NewItemRequest(
                1L,
                "itemName",
                "testDescription",
                true,
                user.getId(),
                null);
        ItemDto createdItem = service.createItem(user.getId(), itemRequest);

        NewBookingRequest bookingRequest = new NewBookingRequest(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                createdItem.getId(),
                userToComment.getId()
        );
        bookingService.createBooking(userToComment.getId(), bookingRequest);

        NewCommentRequest commentRequest = new NewCommentRequest("comment1", createdItem.getId(), userToComment.getId());
        service.addComment(userToComment.getId(), createdItem.getId(), commentRequest);

        ExtendedItemDto itemToInvestigate = service.getItemById(createdItem.getId(), userToComment.getId());

        assertThat(itemToInvestigate.getComments().size(), equalTo(1));
        assertThat(itemToInvestigate.getComments().getFirst().getText(), equalTo(commentRequest.getText()));
    }
}
