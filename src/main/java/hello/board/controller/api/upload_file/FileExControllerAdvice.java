package hello.board.controller.api.upload_file;

import hello.board.dto.ResponseResult;
import hello.board.exception.EmptyFileException;
import hello.board.exception.SaveFileException;
import hello.board.exception.UploadFileNotFoundException;
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
    public ResponseResult uploadFileNotFoundEx(UploadFileNotFoundException e) {
        return new ResponseResult(HttpStatus.NOT_FOUND.toString(), "요청 값에 문제가 있습니다.", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseResult saveFileEx(SaveFileException e) {
        log.error("SaveFileException 오류 발생", e);
        return new ResponseResult(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "서버에 문제가 있습니다.",
                e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseResult emptyFileEx(EmptyFileException e) {
        return new ResponseResult(HttpStatus.BAD_REQUEST.toString(), "요청 값에 문제가 있습니다.", e.getMessage());
    }
}
