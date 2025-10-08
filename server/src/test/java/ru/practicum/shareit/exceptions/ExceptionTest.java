package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.exception.*;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionTest {

    @Test
    void accessDeniedExceptionShouldCreateWithMessageAndStatus() {
        String message = "Access denied";
        AccessDeniedException exception = new AccessDeniedException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class)).isNotNull();
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class).value()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void conflictExceptionShouldCreateWithMessageAndStatus() {
        String message = "Conflict occurred";
        ConflictException exception = new ConflictException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class)).isNotNull();
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class).value()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void duplicatedDataExceptionShouldCreateWithMessage() {
        String message = "Data already exists";
        DuplicatedDataException exception = new DuplicatedDataException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class)).isNull();
    }

    @Test
    void notFoundExceptionShouldCreateWithMessageAndStatus() {
        String message = "Resource not found";
        NotFoundException exception = new NotFoundException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class)).isNotNull();
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class).value()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void notItemOwnerExceptionShouldCreateWithMessageAndStatus() {
        String message = "Not item owner";
        NotItemOwnerException exception = new NotItemOwnerException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class)).isNotNull();
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class).value()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validationExceptionShouldCreateWithMessageAndStatus() {
        String message = "Validation failed";
        ValidationException exception = new ValidationException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class)).isNotNull();
        assertThat(exception.getClass().getAnnotation(ResponseStatus.class).value()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void exceptionsShouldInheritFromRuntimeException() {
        assertThat(new AccessDeniedException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new ConflictException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new DuplicatedDataException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new NotFoundException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new NotItemOwnerException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new ValidationException("test")).isInstanceOf(RuntimeException.class);
    }

    @Test
    void exceptionsWithDifferentMessages() {
        String message1 = "First message";
        String message2 = "Second message";

        AccessDeniedException exception1 = new AccessDeniedException(message1);
        AccessDeniedException exception2 = new AccessDeniedException(message2);

        assertThat(exception1.getMessage()).isEqualTo(message1);
        assertThat(exception2.getMessage()).isEqualTo(message2);
        assertThat(exception1.getMessage()).isNotEqualTo(exception2.getMessage());
    }

    @Test
    void responseStatusAnnotationsShouldBeCorrect() {
        assertThat(AccessDeniedException.class.getAnnotation(ResponseStatus.class).value())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(ConflictException.class.getAnnotation(ResponseStatus.class).value())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(NotFoundException.class.getAnnotation(ResponseStatus.class).value())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(NotItemOwnerException.class.getAnnotation(ResponseStatus.class).value())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(ValidationException.class.getAnnotation(ResponseStatus.class).value())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(DuplicatedDataException.class.getAnnotation(ResponseStatus.class)).isNull();
    }
}