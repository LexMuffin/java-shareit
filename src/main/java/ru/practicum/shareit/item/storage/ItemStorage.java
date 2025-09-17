package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Long itemId);

    Item getItemById(Long itemId);

    Collection<Item> getItemByText(String text);

    Collection<Item> getAllItems();
}
