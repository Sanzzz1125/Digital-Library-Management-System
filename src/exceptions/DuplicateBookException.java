package exceptions;

public class DuplicateBookException extends LibraryException {
    public DuplicateBookException(String title) {
        super("Book '" + title + "' already exists in the library.", 409);
    }
}
