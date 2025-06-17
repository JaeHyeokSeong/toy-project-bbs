package hello.board.web.dto;

import lombok.Data;

@Data
public class ErrorResult {
    private String statusCode;
    private String message;

    public ErrorResult(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
