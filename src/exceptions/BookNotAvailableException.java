package exceptions;

public class BookNotAvailableException extends LibraryException {
    public BookNotAvailableException(String title) {
        super("Book '" + title + "' is currently not available for borrowing.", 503);
    }
}
