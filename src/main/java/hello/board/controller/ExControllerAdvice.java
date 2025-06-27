package hello.board.controller;

import hello.board.controller.dto.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ExControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResult MaxUploadSizeExceededEx(MaxUploadSizeExceededException e) {
        return new ErrorResult("413 Payload Too Large", e.getMessage());
    }
}
