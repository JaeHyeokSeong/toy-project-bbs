package hello.board.controller.api.upload_file;

import hello.board.controller.dto.ErrorResult;
import hello.board.exception.UploadFileNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("hello.board.api.upload_file")
public class FileExControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResult NoSuchElementEx(UploadFileNotFound e) {
        return new ErrorResult(HttpStatus.NOT_FOUND.toString(), e.getMessage());
    }
}
