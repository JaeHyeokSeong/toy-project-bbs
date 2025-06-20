package hello.board.api.upload_file;

import hello.board.web.dto.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice("hello.board.api.upload_file")
public class FileExControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResult NoSuchElementEx(NoSuchElementException e) {
        return new ErrorResult("404 Not Found", e.getMessage());
    }
}
