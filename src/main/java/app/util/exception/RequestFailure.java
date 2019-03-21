package app.util.exception;

public class RequestFailure extends RuntimeException {
    public RequestFailure() { }

    public RequestFailure(String message) {
        super(message);
    }

    public RequestFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestFailure(Throwable cause) {
        super(cause);
    }
}
