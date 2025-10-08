package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequestWhenValidDataShouldCreateItemRequest() {
        Long userId = 1L;
        User user = new User(userId, "user", "user@email.com");
        NewRequest newRequest = new NewRequest("Need a drill", null);
        ItemRequest itemRequest = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createItemRequest(userId, newRequest);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequestWhenUserNotFoundShouldThrowException() {
        Long userId = 1L;
        NewRequest newRequest = new NewRequest("Need a drill", null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.createItemRequest(userId, newRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь c id 1 не найден");
    }

    @Test
    void findItemRequestWhenValidDataShouldReturnItemRequest() {
        Long requestId = 1L;
        User user = new User(1L, "user", "user@email.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "Need a drill", user, LocalDateTime.now());
        List<Item> items = List.of(new Item(1L, "Drill", "Good drill", true, user, requestId));

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);

        ItemRequestDto result = itemRequestService.findItemRequest(requestId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(requestId);
        assertThat(result.getItems()).hasSize(1);
        verify(itemRepository, times(1)).findAllByRequestId(requestId);
    }

    @Test
    void findItemRequestWhenRequestNotFoundShouldThrowException() {
        Long requestId = 1L;

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.findItemRequest(requestId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id 1 не найден");
    }

    @Test
    void findItemRequestWhenNoItemsShouldReturnEmptyItemsList() {
        Long requestId = 1L;
        User user = new User(1L, "user", "user@email.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "Need a drill", user, LocalDateTime.now());

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.findItemRequest(requestId);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void findAllByRequestorIdWhenValidDataShouldReturnRequests() {
        Long requestorId = 1L;
        User user = new User(requestorId, "user", "user@email.com");
        ItemRequest request1 = new ItemRequest(1L, "Need drill", user, LocalDateTime.now().minusDays(1));
        ItemRequest request2 = new ItemRequest(2L, "Need hammer", user, LocalDateTime.now());
        List<ItemRequest> requests = List.of(request1, request2);
        Item item = new Item(1L, "Drill", "Good drill", true, user, 1L);

        when(userRepository.findById(requestorId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorId(requestorId)).thenReturn(requests);
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(item));

        Collection<ItemRequestDto> result = itemRequestService.findAllByRequestorId(requestorId);

        assertThat(result).hasSize(2);
        verify(itemRequestRepository, times(1)).findByRequestorId(requestorId);
    }

    @Test
    void findAllByRequestorIdWhenUserNotFoundShouldThrowException() {
        Long requestorId = 1L;

        when(userRepository.findById(requestorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.findAllByRequestorId(requestorId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь c id 1 не найден");
    }

    @Test
    void findAllByRequestorIdWhenNoRequestsShouldReturnEmptyList() {
        Long requestorId = 1L;
        User user = new User(requestorId, "user", "user@email.com");

        when(userRepository.findById(requestorId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorId(requestorId)).thenReturn(List.of());
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of());

        Collection<ItemRequestDto> result = itemRequestService.findAllByRequestorId(requestorId);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllOfAnotherRequestorsWhenValidDataShouldReturnRequests() {
        Long requestorId = 1L;
        User otherUser = new User(2L, "other", "other@email.com");
        ItemRequest request1 = new ItemRequest(1L, "Need drill", otherUser, LocalDateTime.now().minusDays(1));
        ItemRequest request2 = new ItemRequest(2L, "Need hammer", otherUser, LocalDateTime.now());

        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId))
                .thenReturn(List.of(request1, request2));

        Collection<ItemRequestDto> result = itemRequestService.findAllOfAnotherRequestors(requestorId);

        assertThat(result).hasSize(2);
        verify(itemRequestRepository, times(1)).findByRequestorIdNotOrderByCreatedDesc(requestorId);
    }

    @Test
    void findAllOfAnotherRequestorsWhenNoOtherRequestsShouldReturnEmptyList() {
        Long requestorId = 1L;

        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId))
                .thenReturn(List.of());

        Collection<ItemRequestDto> result = itemRequestService.findAllOfAnotherRequestors(requestorId);

        assertThat(result).isEmpty();
    }

    @Test
    void updateWhenRequestNotFoundShouldThrowException() {
        Long userId = 1L;
        Long requestId = 1L;
        UpdateRequest updateRequest = new UpdateRequest(requestId, "Updated description", null, null);

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.update(userId, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id 1 не найден");
    }

    @Test
    void deleteWhenValidDataShouldDeleteRequest() {
        Long requestId = 1L;
        User user = new User(1L, "user", "user@email.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "Need drill", user, LocalDateTime.now());

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        doNothing().when(itemRequestRepository).delete(itemRequest);

        itemRequestService.delete(requestId);

        verify(itemRequestRepository, times(1)).delete(itemRequest);
    }

    @Test
    void deleteWhenRequestNotFoundShouldThrowException() {
        Long requestId = 1L;

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.delete(requestId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id 1 не найден");
    }

    @Test
    void getRequestsDataWhenMultipleRequestsWithItemsShouldGroupItemsCorrectly() {
        Long requestorId = 1L;
        User user = new User(requestorId, "user", "user@email.com");
        ItemRequest request1 = new ItemRequest(1L, "Need drill", user, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(2L, "Need hammer", user, LocalDateTime.now());
        List<ItemRequest> requests = List.of(request1, request2);
        Item item1 = new Item(1L, "Drill", "Good drill", true, user, 1L);
        Item item2 = new Item(2L, "Hammer", "Good hammer", true, user, 2L);

        when(userRepository.findById(requestorId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorId(requestorId)).thenReturn(requests);
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(item1, item2));

        Collection<ItemRequestDto> result = itemRequestService.findAllByRequestorId(requestorId);

        assertThat(result).hasSize(2);
        assertThat(result.stream().filter(r -> r.getId().equals(1L)).findFirst().get().getItems()).hasSize(1);
        assertThat(result.stream().filter(r -> r.getId().equals(2L)).findFirst().get().getItems()).hasSize(1);
    }

    @Test
    void getRequestsDataWhenRequestsWithoutItemsShouldReturnEmptyItems() {
        Long requestorId = 1L;
        User user = new User(requestorId, "user", "user@email.com");
        ItemRequest request1 = new ItemRequest(1L, "Need drill", user, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(2L, "Need hammer", user, LocalDateTime.now());
        List<ItemRequest> requests = List.of(request1, request2);

        when(userRepository.findById(requestorId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorId(requestorId)).thenReturn(requests);
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of());

        Collection<ItemRequestDto> result = itemRequestService.findAllByRequestorId(requestorId);

        assertThat(result).hasSize(2);
        assertThat(result.stream().allMatch(r -> r.getItems().isEmpty())).isTrue();
    }
}