package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotItemOwnerException;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ExtendedItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtendedItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ExtendedItemServiceImpl itemService;

    @Test
    void createItemWhenValidDataShouldCreateItem() {
        Long ownerId = 1L;
        User owner = new User(1L, "owner", "owner@email.com");
        NewItemRequest request = new NewItemRequest(null, "item", "description", true, null, null);
        Item item = new Item(1L, "item", "description", true, owner, null);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(ownerId, request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("item");
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItemWhenUserNotFoundShouldThrowException() {
        Long ownerId = 1L;
        NewItemRequest request = new NewItemRequest(null, "item", "description", true, null, null);

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(ownerId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь c id 1 не найден");
    }

    @Test
    void updateItemWhenValidDataShouldUpdateItem() {
        Long itemId = 1L;
        Long ownerId = 1L;
        User owner = new User(ownerId, "owner", "owner@email.com");
        Item item = new Item(itemId, "old name", "old description", false, owner, null);
        UpdateItemRequest request = new UpdateItemRequest(null, "new name", "new description", true, null, null);
        Item updatedItem = new Item(itemId, "new name", "new description", true, owner, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(itemId, request, ownerId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("new name");
        assertThat(result.getDescription()).isEqualTo("new description");
        assertThat(result.getAvailable()).isTrue();
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemWhenItemNotFoundShouldThrowException() {
        Long itemId = 1L;
        Long ownerId = 1L;
        UpdateItemRequest request = new UpdateItemRequest(null, "new name", "new description", true, null, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(itemId, request, ownerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь c id 1 не найдена");
    }

    @Test
    void updateItemWhenUserNotOwnerShouldThrowException() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long differentOwnerId = 2L;
        User owner = new User(differentOwnerId, "owner", "owner@email.com");
        Item item = new Item(itemId, "item", "description", true, owner, null);
        UpdateItemRequest request = new UpdateItemRequest(null, "new name", "new description", true, null, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User(ownerId, "user", "user@email.com")));

        assertThatThrownBy(() -> itemService.updateItem(itemId, request, ownerId))
                .isInstanceOf(NotItemOwnerException.class)
                .hasMessageContaining("Редактировать данные вещи может только её владелец");
    }

    @Test
    void deleteItemWhenValidDataShouldDeleteItem() {
        Long itemId = 1L;

        doNothing().when(itemRepository).deleteById(itemId);

        itemService.deleteItem(itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void getItemByIdWhenNonOwnerRequestsShouldReturnExtendedItemWithoutBookings() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requesterId = 2L;
        User owner = new User(ownerId, "owner", "owner@email.com");
        Item item = new Item(itemId, "item", "description", true, owner, null);
        Comment comment = new Comment(1L, "comment", item, owner, LocalDateTime.now());
        comment.setAuthor(owner);
        comment.setItem(item);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));

        ExtendedItemDto result = itemService.getItemById(itemId, requesterId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getItemByIdWhenItemNotFoundShouldThrowException() {
        Long itemId = 1L;
        Long ownerId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemById(itemId, ownerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь c id 1 не найдена");
    }

    @Test
    void getItemByTextWhenTextIsBlankShouldReturnEmptyList() {
        String text = " ";

        Collection<ItemDto> result = itemService.getItemByText(text);

        assertThat(result).isEmpty();
    }

    @Test
    void getItemByTextWhenTextIsNotEmptyShouldReturnItems() {
        String text = "item";
        User owner = new User(1L, "owner", "owner@email.com");
        Item item = new Item(1L, "item", "description", true, owner, null);

        when(itemRepository.findByText(text)).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.getItemByText(text);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo("item");
        verify(itemRepository, times(1)).findByText(text);
    }

    @Test
    void getAllItemsByIdWhenOwnerHasNoItemsShouldReturnEmptyList() {
        Long ownerId = 1L;

        when(itemRepository.findAllByOwnerId(ownerId)).thenReturn(List.of());

        Collection<ExtendedItemDto> result = itemService.getAllItemsById(ownerId);

        assertThat(result).isEmpty();
        verify(itemRepository, times(1)).findAllByOwnerId(ownerId);
    }

    @Test
    void addCommentWhenUserNotFoundShouldThrowException() {
        Long authorId = 1L;
        Long itemId = 1L;
        NewCommentRequest request = new NewCommentRequest("comment", 1L, 1L);

        when(userRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(authorId, itemId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь c id 1 не найден");
    }

    @Test
    void addCommentWhenItemNotFoundShouldThrowException() {
        Long authorId = 1L;
        Long itemId = 1L;
        User author = new User(authorId, "author", "author@email.com");
        NewCommentRequest request = new NewCommentRequest("comment", 1L, 1L);

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(authorId, itemId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь c id 1 не найдена");
    }
}