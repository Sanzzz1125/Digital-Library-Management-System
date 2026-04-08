package exceptions;

public class BookNotFoundException extends LibraryException {
    private int bookId;

    public BookNotFoundException(int bookId) {
        super("Book with ID " + bookId + " not found in the library.", 404);
        this.bookId = bookId;
    }

    public BookNotFoundException(String title) {
        super("Book titled '" + title + "' not found in the library.", 404);
        this.bookId = -1;
    }

    public int getBookId() {
        return bookId;
    }
}