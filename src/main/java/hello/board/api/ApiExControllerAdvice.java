package hello.board.api;

import hello.board.exception.BindingResultException;
import hello.board.controller.dto.ErrorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

@RestControllerAdvice("hello.board.api")
@RequiredArgsConstructor
public class ApiExControllerAdvice {

    private final MessageSource messageSource;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult bindingResultEx(BindingResultException e) {

        List<ObjectError> errors = e.getErrors();
        List<String> message = errors.stream()
                .map(oe -> messageSource.getMessage(oe, Locale.KOREAN))
                .toList();

        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(), message);
    }
}
