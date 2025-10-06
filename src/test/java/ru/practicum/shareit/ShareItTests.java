package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.UserServiceImplIntegrationTest;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
class ShareItTests {

	@Test
	void contextLoads() {
	}
}
