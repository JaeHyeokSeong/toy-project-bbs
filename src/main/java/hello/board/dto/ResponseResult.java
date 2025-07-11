package hello.board.dto;

import lombok.Data;

@Data
public class ResponseResult {

    private String statusCode;
    private Object message;
    private Object data;

    public ResponseResult(String statusCode, Object message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
}
