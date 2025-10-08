package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ItemRequestServiceImpl.class)
class ItemRequestServiceImplSliceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void findByRequestorIdShouldReturnUserRequests() {
        User user = createUser("user1", "user1@email.com");
        User otherUser = createUser("user2", "user2@email.com");
        ItemRequest request1 = createItemRequest("Need drill", user, LocalDateTime.now().minusDays(1));
        ItemRequest request2 = createItemRequest("Need hammer", user, LocalDateTime.now());
        createItemRequest("Need saw", otherUser, LocalDateTime.now());

        List<ItemRequest> result = itemRequestRepository.findByRequestorId(user.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemRequest::getDescription)
                .containsExactlyInAnyOrder("Need drill", "Need hammer");
    }

    @Test
    void findByRequestorIdNotOrderByCreatedDescShouldReturnOtherUsersRequests() {
        User user1 = createUser("user1", "user1@email.com");
        User user2 = createUser("user2", "user2@email.com");
        User user3 = createUser("user3", "user3@email.com");

        createItemRequest("Request 1", user1, LocalDateTime.now().minusDays(2));
        createItemRequest("Request 2", user2, LocalDateTime.now().minusDays(1));
        createItemRequest("Request 3", user3, LocalDateTime.now());

        List<ItemRequest> result = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(user1.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemRequest::getDescription)
                .containsExactly("Request 3", "Request 2");
    }

    @Test
    void findAllByRequestIdShouldReturnItemsForRequest() {
        User user = createUser("user", "user@email.com");
        ItemRequest request = createItemRequest("Need tools", user, LocalDateTime.now());
        Item item1 = createItem("Drill", "Good drill", true, user, request.getId());
        Item item2 = createItem("Hammer", "Good hammer", true, user, request.getId());
        createItem("Saw", "Good saw", true, user, null); // Item without request

        List<Item> result = itemRepository.findAllByRequestId(request.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Item::getName)
                .containsExactlyInAnyOrder("Drill", "Hammer");
    }

    @Test
    void findAllByRequestIdInShouldReturnItemsForMultipleRequests() {
        User user = createUser("user", "user@email.com");
        ItemRequest request1 = createItemRequest("Request 1", user, LocalDateTime.now());
        ItemRequest request2 = createItemRequest("Request 2", user, LocalDateTime.now());

        Item item1 = createItem("Drill", "For request 1", true, user, request1.getId());
        Item item2 = createItem("Hammer", "For request 2", true, user, request2.getId());
        Item item3 = createItem("Saw", "No request", true, user, null);

        List<Item> result = itemRepository.findAllByRequestIdIn(List.of(request1.getId(), request2.getId()));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Item::getName)
                .containsExactlyInAnyOrder("Drill", "Hammer");
    }

    @Test
    void saveItemRequestShouldPersistCorrectly() {
        User user = createUser("user", "user@email.com");
        ItemRequest request = new ItemRequest();
        request.setDescription("Need new tool");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Need new tool");
        assertThat(saved.getRequestor()).isEqualTo(user);
        assertThat(saved.getCreated()).isNotNull();
    }

    @Test
    void findByIdShouldReturnRequestWhenExists() {
        User user = createUser("user", "user@email.com");
        ItemRequest request = createItemRequest("Need tool", user, LocalDateTime.now());

        ItemRequest found = itemRequestRepository.findById(request.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getDescription()).isEqualTo("Need tool");
        assertThat(found.getRequestor()).isEqualTo(user);
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotExists() {
        assertThat(itemRequestRepository.findById(999L)).isEmpty();
    }

    @Test
    void deleteItemRequestShouldRemoveFromDatabase() {
        User user = createUser("user", "user@email.com");
        ItemRequest request = createItemRequest("To be deleted", user, LocalDateTime.now());

        itemRequestRepository.delete(request);
        entityManager.flush();

        assertThat(itemRequestRepository.findById(request.getId())).isEmpty();
    }

    @Test
    void findByRequestorIdWithNoRequestsShouldReturnEmptyList() {
        User user = createUser("user", "user@email.com");

        List<ItemRequest> result = itemRequestRepository.findByRequestorId(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByRequestIdWithNoItemsShouldReturnEmptyList() {
        User user = createUser("user", "user@email.com");
        ItemRequest request = createItemRequest("Empty request", user, LocalDateTime.now());

        List<Item> result = itemRepository.findAllByRequestId(request.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void itemRequestCascadeOperations() {
        User user = createUser("user", "user@email.com");
        ItemRequest request = createItemRequest("Test request", user, LocalDateTime.now());
        Item item = createItem("Test item", "Description", true, user, request.getId());

        // Verify relationship
        assertThat(item.getRequestId()).isEqualTo(request.getId());

        // Verify we can find items by request
        List<Item> items = itemRepository.findAllByRequestId(request.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Test item");
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private ItemRequest createItemRequest(String description, User requestor, LocalDateTime created) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequestor(requestor);
        request.setCreated(created);
        return entityManager.persistAndFlush(request);
    }

    private Item createItem(String name, String description, Boolean available, User owner, Long requestId) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequestId(requestId);
        return entityManager.persistAndFlush(item);
    }
}