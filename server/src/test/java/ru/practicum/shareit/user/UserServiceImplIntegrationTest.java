package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5433/share_it_test")
@ActiveProfiles("test")
public class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService service;

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
    public void testSaveUser() {
        NewUserRequest newUser = new NewUserRequest("testName", "testEmail@mail.com");
        service.createUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", newUser.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUser.getName()));
        assertThat(user.getEmail(), equalTo(newUser.getEmail()));
    }

    @Test
    public void testUpdateUser() {
        NewUserRequest newUser = new NewUserRequest("testName5", "testEmail5@mail.com");
        UserDto savedUser = service.createUser(newUser);

        UpdateUserRequest updateUser = new UpdateUserRequest(1L, "newTestName5", "NewTestEmail5@mail.com");
        service.updateUser(savedUser.getId(), updateUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", updateUser.getEmail())
                .getSingleResult();

        assertThat(user.getName(), equalTo(updateUser.getName()));
        assertThat(user.getEmail(), equalTo(updateUser.getEmail()));
    }

    @Test
    public void testDeleteUser() {
        NewUserRequest newUser = new NewUserRequest("testName", "testEmail@mail.com");
        UserDto savedUser = service.createUser(newUser);

        service.deleteUser(savedUser.getId());

        assertThrows(NotFoundException.class, () -> service.getUserById(savedUser.getId()));
    }

    @Test
    public void testGetUserById() {
        NewUserRequest newUser = new NewUserRequest("testName", "testEmail@mail.com");
        UserDto savedUser = service.createUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", savedUser.getId())
                .getSingleResult();

        assertThat(user.getId(), equalTo(savedUser.getId()));
        assertThat(user.getName(), equalTo(savedUser.getName()));
        assertThat(user.getEmail(), equalTo(savedUser.getEmail()));
    }

    @Test
    public void testGetAllUsers() {
        NewUserRequest newUser = new NewUserRequest("testName", "testEmail@mail.com");
        service.createUser(newUser);

        NewUserRequest newUser2 = new NewUserRequest("testName2", "testEmail2@mail.com");
        service.createUser(newUser2);

        TypedQuery<Long> query = em.createQuery("Select count(u) from User u", Long.class);
        Long usersCount = query.getSingleResult();

        assertThat(usersCount, equalTo(2L));
    }
}
