package hello.board.controller.dto;

import lombok.Data;

@Data
public class ErrorResult {
    private String statusCode;
    private Object message;

    public ErrorResult(String statusCode, Object message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
