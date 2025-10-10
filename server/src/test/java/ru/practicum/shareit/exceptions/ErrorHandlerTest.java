package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.handler.ErrorHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ErrorHandlerTest.TestController.class, ErrorHandler.class})
@ActiveProfiles("test")
class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ErrorHandler errorHandler;

    @MockBean
    private BookingService bookingService;

    @RestController
    static class TestController {

        @GetMapping("/test/validation")
        public String testValidation() {
            throw new ValidationException("Validation test message");
        }

        @GetMapping("/test/not-found")
        public String testNotFound() {
            throw new NotFoundException("Not found test message");
        }

        @GetMapping("/test/duplicated-data")
        public String testDuplicatedData() {
            throw new DuplicatedDataException("Duplicated data test message");
        }

        @GetMapping("/test/conflict")
        public String testConflict() {
            throw new ConflictException("Conflict test message");
        }

        @GetMapping("/test/not-item-owner")
        public String testNotItemOwner() {
            throw new NotItemOwnerException("Not item owner test message");
        }

        @GetMapping("/test/throwable")
        public String testThrowable() {
            throw new RuntimeException("Throwable test message");
        }

        @GetMapping("/test/illegal-argument")
        public String testIllegalArgument() {
            throw new IllegalArgumentException("Illegal argument test message");
        }

        @GetMapping("/test/null-pointer")
        public String testNullPointer() {
            throw new NullPointerException("Null pointer test message");
        }

        @GetMapping("/test/ok")
        public String testOk() {
            return "OK";
        }
    }

    // Unit тесты для методов ErrorHandler
    @Test
    void handleValidationShouldReturnBadRequestResponse() {
        ValidationException exception = new ValidationException("Validation test message");

        ErrorResponse response = errorHandler.handleValidation(exception);

        assertNotNull(response);
        assertEquals("Validation test message", response.getError());
    }

    @Test
    void handleNotFoundShouldReturnNotFoundResponse() {
        NotFoundException exception = new NotFoundException("Not found test message");

        ErrorResponse response = errorHandler.handleNotFound(exception);

        assertNotNull(response);
        assertEquals("Not found test message", response.getError());
    }

    @Test
    void handleDuplicatedDataShouldReturnNotFoundResponse() {
        DuplicatedDataException exception = new DuplicatedDataException("Duplicated data test message");

        ErrorResponse response = errorHandler.handleDuplicatedData(exception);

        assertNotNull(response);
        assertEquals("Duplicated data test message", response.getError());
    }

    @Test
    void handleConflictShouldReturnConflictResponse() {
        ConflictException exception = new ConflictException("Conflict test message");

        ErrorResponse response = errorHandler.handleConflict(exception);

        assertNotNull(response);
        assertEquals("Conflict test message", response.getError());
    }

    @Test
    void handleNotItemOwnerExceptionShouldReturnForbiddenResponse() {
        NotItemOwnerException exception = new NotItemOwnerException("Not item owner test message");

        ErrorResponse response = errorHandler.handleNotItemOwnerException(exception);

        assertNotNull(response);
        assertEquals("Not item owner test message", response.getError());
    }

    @Test
    void handleThrowableShouldReturnInternalServerErrorResponse() {
        RuntimeException exception = new RuntimeException("Throwable test message");

        ErrorResponse response = errorHandler.handleThrowable(exception);

        assertNotNull(response);
        assertEquals("Произошла непредвиденная ошибка.", response.getError());
    }


    @Test
    void handleThrowableThroughMockMvcShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/throwable"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Произошла непредвиденная ошибка."));
    }

    @Test
    void handleIllegalArgumentExceptionThroughMockMvcShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Произошла непредвиденная ошибка."));
    }

    @Test
    void handleNullPointerExceptionThroughMockMvcShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/null-pointer"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Произошла непредвиденная ошибка."));
    }

    // Тесты для граничных случаев
    @Test
    void errorResponseShouldSetAndGetError() {
        ErrorResponse errorResponse = new ErrorResponse("Test error message");

        assertEquals("Test error message", errorResponse.getError());
    }

    @Test
    void errorResponseShouldHandleNullError() {
        ErrorResponse errorResponse = new ErrorResponse(null);

        assertNull(errorResponse.getError());
    }

    @Test
    void errorResponseShouldHandleEmptyError() {
        ErrorResponse errorResponse = new ErrorResponse("");

        assertEquals("", errorResponse.getError());
    }

    @Test
    void handleExceptionWithLongErrorMessage() {
        String longMessage = "A".repeat(1000);
        ConflictException exception = new ConflictException(longMessage);

        ErrorResponse response = errorHandler.handleConflict(exception);

        assertNotNull(response);
        assertEquals(longMessage, response.getError());
    }

    @Test
    void handleExceptionWithSpecialCharactersInErrorMessage() {
        String specialMessage = "Error with special chars: \n\t\"'\\";
        ConflictException exception = new ConflictException(specialMessage);

        ErrorResponse response = errorHandler.handleConflict(exception);

        assertNotNull(response);
        assertEquals(specialMessage, response.getError());
    }

    @Test
    void handleExceptionWithoutMessage() {
        ConflictException exception = new ConflictException(null);

        ErrorResponse response = errorHandler.handleConflict(exception);

        assertNotNull(response);
        assertNull(response.getError());
    }

    @Test
    void handleExceptionWithMultilineErrorMessage() {
        String multilineMessage = "First line\nSecond line\nThird line";
        ConflictException exception = new ConflictException(multilineMessage);

        ErrorResponse response = errorHandler.handleConflict(exception);

        assertNotNull(response);
        assertEquals(multilineMessage, response.getError());
    }

    @Test
    void handleNullPointerExceptionAsThrowable() {
        NullPointerException exception = new NullPointerException("Null pointer test message");

        ErrorResponse response = errorHandler.handleThrowable(exception);

        assertNotNull(response);
        assertEquals("Произошла непредвиденная ошибка.", response.getError());
    }

    @Test
    void handleIllegalArgumentExceptionAsThrowable() {
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument test message");

        ErrorResponse response = errorHandler.handleThrowable(exception);

        assertNotNull(response);
        assertEquals("Произошла непредвиденная ошибка.", response.getError());
    }

    @Test
    void handleValidationExceptionWithEmptyMessage() {
        ValidationException exception = new ValidationException("");

        ErrorResponse response = errorHandler.handleValidation(exception);

        assertNotNull(response);
        assertEquals("", response.getError());
    }

    @Test
    void handleNotFoundExceptionWithWhitespaceMessage() {
        NotFoundException exception = new NotFoundException("   ");

        ErrorResponse response = errorHandler.handleNotFound(exception);

        assertNotNull(response);
        assertEquals("   ", response.getError());
    }

    @Test
    void handleMultipleExceptionsSequentially() {
        ConflictException conflictException = new ConflictException("Conflict message");
        NotFoundException notFoundException = new NotFoundException("Not found message");
        ValidationException validationException = new ValidationException("Validation message");

        ErrorResponse conflictResponse = errorHandler.handleConflict(conflictException);
        ErrorResponse notFoundResponse = errorHandler.handleNotFound(notFoundException);
        ErrorResponse validationResponse = errorHandler.handleValidation(validationException);

        assertEquals("Conflict message", conflictResponse.getError());
        assertEquals("Not found message", notFoundResponse.getError());
        assertEquals("Validation message", validationResponse.getError());
    }
}