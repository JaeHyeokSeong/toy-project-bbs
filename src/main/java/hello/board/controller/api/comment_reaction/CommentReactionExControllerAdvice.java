package hello.board.controller.api.comment_reaction;

import hello.board.exception.CommentNotFoundException;
import hello.board.dto.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("hello.board.controller.api.comment_reaction")
public class CommentReactionExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseResult commentNotFoundEx(CommentNotFoundException e) {
        return new ResponseResult(HttpStatus.BAD_REQUEST.toString(), "요청 값에 문제가 있습니다.", e.getMessage());
    }
}
