package hello.board.controller.api.member;

import hello.board.controller.dto.ErrorResult;
import hello.board.exception.MemberThumbnailFileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(name = "hello.board.controller.api.member")
public class MemberExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult memberThumbnailFileNotFoundEx(MemberThumbnailFileNotFoundException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult methodArgumentTypeMismatchEx(MethodArgumentTypeMismatchException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(),
                "요청 값에 문제가 있습니다. memberId가 올바르지 않습니다: value = " + e.getValue());
    }
}
