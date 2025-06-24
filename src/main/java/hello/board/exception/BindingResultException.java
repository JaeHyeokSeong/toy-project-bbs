package hello.board.exception;

import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

@Getter
public class BindingResultException extends RuntimeException {

    private final List<ObjectError> errors;

    public BindingResultException(List<ObjectError> errors) {
        this.errors = errors;
    }
}
