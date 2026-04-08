package exceptions;

public class LibraryException extends Exception {
    private int errorCode;

    public LibraryException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}