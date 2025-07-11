package hello.board.exception;

public class UploadFileNotFoundException extends RuntimeException {
    public UploadFileNotFoundException() {
        super();
    }

    public UploadFileNotFoundException(String message) {
        super(message);
    }

    public UploadFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadFileNotFoundException(Throwable cause) {
        super(cause);
    }

    protected UploadFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
