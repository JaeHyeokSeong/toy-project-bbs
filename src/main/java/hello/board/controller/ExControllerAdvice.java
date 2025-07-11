package hello.board.controller;

import hello.board.dto.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ExControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseResult MaxUploadSizeExceededEx(MaxUploadSizeExceededException e) {
        return new ResponseResult("413 Payload Too Large", "요청 값에 문제가 있습니다.", e.getMessage());
    }
}
