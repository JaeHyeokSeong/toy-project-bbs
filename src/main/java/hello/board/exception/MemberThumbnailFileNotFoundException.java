package hello.board.exception;

public class MemberThumbnailFileNotFoundException extends RuntimeException {
    public MemberThumbnailFileNotFoundException() {
        super();
    }

    public MemberThumbnailFileNotFoundException(String message) {
        super(message);
    }

    public MemberThumbnailFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberThumbnailFileNotFoundException(Throwable cause) {
        super(cause);
    }

    protected MemberThumbnailFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
