package exception;

public class IntegrityException extends RuntimeException {
    public IntegrityException() { super(); }
    public IntegrityException(String message) { super(message); }
}
