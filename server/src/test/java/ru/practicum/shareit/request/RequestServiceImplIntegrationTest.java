package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5433/share_it_test")
@ActiveProfiles("test")
public class RequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService requestService;

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

    private void newUserCreation() {
        Query userQuery = em.createNativeQuery("INSERT INTO Users (id, name, email) " +
                "VALUES (:id, :name, :email);");
        userQuery.setParameter("id", 1L);
        userQuery.setParameter("name", "testName");
        userQuery.setParameter("email", "testEmail@email.com");
        userQuery.executeUpdate();
    }

    private void newUser2Creation() {
        Query userQuery = em.createNativeQuery("INSERT INTO Users (id, name, email) " +
                "VALUES (:id, :name, :email);");
        userQuery.setParameter("id", 2L);
        userQuery.setParameter("name", "testName2");
        userQuery.setParameter("email", "testEmail2@email.com");
        userQuery.executeUpdate();
    }

    private void newRequestCreation() {
        Query requestQuery = em.createNativeQuery("INSERT INTO Requests (id, description, requestor_id, created) " +
                "VALUES (:id, :description, :requestor_id, :created);");
        requestQuery.setParameter("id", 1L);
        requestQuery.setParameter("description", "Нужна дрель");
        requestQuery.setParameter("requestor_id", 1L);
        requestQuery.setParameter("created", LocalDateTime.now());
        requestQuery.executeUpdate();
    }

    @Test
    public void testCreateItemRequest() {
        newUserCreation();

        NewRequest newRequest = new NewRequest("Нужна дрель для ремонта", 1L);
        ItemRequestDto request = requestService.createItemRequest(1L, newRequest);

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(newRequest.getDescription()));
        assertThat(request.getRequestorId(), equalTo(1L));
    }

    @Test
    public void testFindItemRequest() {
        newUserCreation();
        newRequestCreation();

        ItemRequestDto request = requestService.findItemRequest(1L);

        assertThat(request.getId(), equalTo(1L));
        assertThat(request.getDescription(), equalTo("Нужна дрель"));
        assertThat(request.getRequestorId(), equalTo(1L));
    }

    @Test
    public void testFindAllByRequestorId() {
        newUserCreation();
        newRequestCreation();

        Query requestQuery2 = em.createNativeQuery("INSERT INTO Requests (id, description, requestor_id, created) " +
                "VALUES (:id, :description, :requestor_id, :created);");
        requestQuery2.setParameter("id", 2L);
        requestQuery2.setParameter("description", "Нужен молоток");
        requestQuery2.setParameter("requestor_id", 1L);
        requestQuery2.setParameter("created", LocalDateTime.now().plusHours(1));
        requestQuery2.executeUpdate();

        List<ItemRequestDto> requests = requestService.findAllByRequestorId(1L).stream().toList();

        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getId(), equalTo(2L));
        assertThat(requests.get(0).getDescription(), equalTo("Нужен молоток"));
        assertThat(requests.get(1).getId(), equalTo(1L));
        assertThat(requests.get(1).getDescription(), equalTo("Нужна дрель"));
    }

    @Test
    public void testFindAllOfAnotherRequestors() {
        newUserCreation();
        newUser2Creation();
        newRequestCreation();

        Query requestQuery2 = em.createNativeQuery("INSERT INTO Requests (id, description, requestor_id, created) " +
                "VALUES (:id, :description, :requestor_id, :created);");
        requestQuery2.setParameter("id", 2L);
        requestQuery2.setParameter("description", "Нужен молоток");
        requestQuery2.setParameter("requestor_id", 2L);
        requestQuery2.setParameter("created", LocalDateTime.now().plusHours(1));
        requestQuery2.executeUpdate();

        List<ItemRequestDto> requests = requestService.findAllOfAnotherRequestors(1L).stream().toList();

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.getFirst().getId(), equalTo(2L));
        assertThat(requests.getFirst().getDescription(), equalTo("Нужен молоток"));
        assertThat(requests.getFirst().getRequestorId(), equalTo(2L));
    }

    @Test
    public void testDeleteRequest() {
        newUserCreation();
        newRequestCreation();

        requestService.delete(1L);

        TypedQuery<ItemRequest> query = em.createQuery("SELECT r FROM ItemRequest r WHERE r.id = :id", ItemRequest.class);
        query.setParameter("id", 1L);

        assertThrows(NoResultException.class, query::getSingleResult);
    }
}
