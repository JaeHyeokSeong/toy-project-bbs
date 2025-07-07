package hello.board.controller.api.upload_file;

import hello.board.controller.dto.ErrorResult;
import hello.board.exception.EmptyFileException;
import hello.board.exception.SaveFileException;
import hello.board.exception.UploadFileNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("hello.board.controller.api.upload_file")
public class FileExControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResult noSuchElementEx(UploadFileNotFound e) {
        return new ErrorResult(HttpStatus.NOT_FOUND.toString(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult saveFileException(SaveFileException e) {
        log.error("SaveFileException 오류 발생", e);
        return new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult emptyFileException(EmptyFileException e) {
        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }
}
