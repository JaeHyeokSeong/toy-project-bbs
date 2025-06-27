package hello.board.exception;

public class UploadFileNotFound extends RuntimeException {
    public UploadFileNotFound() {
        super();
    }

    public UploadFileNotFound(String message) {
        super(message);
    }

    public UploadFileNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadFileNotFound(Throwable cause) {
        super(cause);
    }

    protected UploadFileNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
