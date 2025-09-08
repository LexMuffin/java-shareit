package ru.practicum.shareit.user.storage;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersEmailsSet = new HashSet<>();

    @PostConstruct
    public void initEmailSet() {
        users.values().forEach(user -> usersEmailsSet.add(user.getEmail()));
    }

    private Long getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        usersEmailsSet.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        User existingUser = users.get(user.getId());
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        usersEmailsSet.add(existingUser.getEmail());
        return existingUser;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с идентификатором - %d не найден", userId)));
    }

    @Override
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean isUserEmailExists(String email) {
        return usersEmailsSet.contains(email);
    }
}
