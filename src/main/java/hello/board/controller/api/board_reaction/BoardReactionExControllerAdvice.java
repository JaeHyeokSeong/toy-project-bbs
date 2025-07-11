package hello.board.controller.api.board_reaction;

import hello.board.dto.ResponseResult;
import hello.board.exception.BoardNotFoundException;
import hello.board.exception.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice("hello.board.controller.api.board_reaction")
public class BoardReactionExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseResult boardNotFoundEx(BoardNotFoundException e) {
        return new ResponseResult(HttpStatus.BAD_REQUEST.toString(), "요청 값에 문제가 있습니다.", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseResult memberNotFoundEx(MemberNotFoundException e) {
        return new ResponseResult(HttpStatus.BAD_REQUEST.toString(), "요청 값에 문제가 있습니다.", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseResult methodArgumentTypeMismatchEx(MethodArgumentTypeMismatchException e) {
        return new ResponseResult(HttpStatus.BAD_REQUEST.toString(),
                "요청 값에 문제가 있습니다.",
                "올바른 형식의 boardId가 아닙니다. 전달된 boardId: " + e.getValue());
    }
}
