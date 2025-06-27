package hello.board.controller.api.comment;

import hello.board.controller.dto.ErrorResult;
import hello.board.exception.BoardNotFoundException;
import hello.board.exception.CommentNotFoundException;
import hello.board.exception.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("hello.board.api.comment")
public class CommentExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult NoAccessCommentEx(CommentNotFoundException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult NoAccessCommentEx(MemberNotFoundException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult NoAccessCommentEx(BoardNotFoundException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }
}
