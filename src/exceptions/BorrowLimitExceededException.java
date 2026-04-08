package exceptions;

public class BorrowLimitExceededException extends LibraryException {
    public BorrowLimitExceededException(String memberName, int limit) {
        super("Member '" + memberName + "' has reached the borrow limit of " + limit + " books.", 429);
    }
}
