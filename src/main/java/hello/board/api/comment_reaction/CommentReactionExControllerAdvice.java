package hello.board.api.comment_reaction;

import hello.board.exception.CommentNotFoundException;
import hello.board.web.dto.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("hello.board.api.comment_reaction")
public class CommentReactionExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult commentNotFoundEx(CommentNotFoundException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }
}
