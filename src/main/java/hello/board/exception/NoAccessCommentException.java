package hello.board.exception;

public class NoAccessCommentException extends RuntimeException {
    public NoAccessCommentException() {
        super();
    }

    public NoAccessCommentException(String message) {
        super(message);
    }

    public NoAccessCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAccessCommentException(Throwable cause) {
        super(cause);
    }

    protected NoAccessCommentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
