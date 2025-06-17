package hello.board.web.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice("hello.board.web.board")
public class BoardExControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String MethodArgumentTypeMismatchEx(MethodArgumentTypeMismatchException e) {
        log.info("MethodArgumentTypeMismatchEx 호출");
        return "error/404";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public String NoSuchElementEx(NoSuchElementException e) {
        return "error/404";
    }
}
