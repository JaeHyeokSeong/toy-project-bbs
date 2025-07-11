package hello.board.controller.api.comment;

import hello.board.dto.ResponseResult;
import hello.board.exception.BoardNotFoundException;
import hello.board.exception.CommentNotFoundException;
import hello.board.exception.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("hello.board.controller.api.comment")
public class CommentExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseResult commentNotFoundEx(CommentNotFoundException e) {
        return new ResponseResult(HttpStatus.BAD_REQUEST.toString(), "요청 값에 문제가 있습니다.", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseResult memberNotFoundEx(MemberNotFoundException e) {
        return new ResponseResult(HttpStatus.BAD_REQUEST.toString(), "요청 값에 문제가 있습니다.", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseResult boardNotFoundEx(BoardNotFoundException e) {
        return new ResponseResult(HttpStatus.BAD_REQUEST.toString(), "요청 값에 문제가 있습니다.", e.getMessage());
    }
}
