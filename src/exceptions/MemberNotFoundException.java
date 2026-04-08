package exceptions;

public class MemberNotFoundException extends LibraryException {
    public MemberNotFoundException(int memberId) {
        super("Member with ID " + memberId + " not found.", 404);
    }
}
