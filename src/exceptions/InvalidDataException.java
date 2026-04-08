package exceptions;

public class InvalidDataException extends LibraryException {
    public InvalidDataException(String field, String value) {
        super("Invalid value '" + value + "' for field: " + field, 400);
    }
}
