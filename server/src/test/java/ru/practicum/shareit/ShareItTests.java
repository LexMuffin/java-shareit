package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ActiveProfiles("test")
class ShareItTests {

	@Test
	void main_shouldStartApplication() {
		String[] args = new String[]{};

		try (var mockedStatic = mockStatic(SpringApplication.class)) {
			ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
			mockedStatic.when(() -> SpringApplication.run(ShareItServer.class, args))
					.thenReturn(mockContext);

			ShareItServer.main(args);

			mockedStatic.verify(() -> SpringApplication.run(ShareItServer.class, args));
		}
	}

	@Test
	void contextLoads() {
		assertNotNull(ShareItServer.class);
	}

}
