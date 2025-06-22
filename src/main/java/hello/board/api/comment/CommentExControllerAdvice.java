package hello.board.api.comment;

import hello.board.web.dto.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommentExControllerAdvice {

    @ExceptionHandler
    public ErrorResult illegalArgumentEx(IllegalArgumentException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }
}
