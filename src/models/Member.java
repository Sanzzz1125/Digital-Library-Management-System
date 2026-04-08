package models;

import java.io.Serializable;
import java.util.ArrayList;

public class Member implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int memberCounter = 100;

    protected int memberId;
    protected String name;
    protected String email;
    protected String phone;
    protected ArrayList<Integer> borrowedBookIds;
    protected int maxBooksAllowed;
    protected String memberType;

    public Member(String name, String email, String phone) {
        this.memberId = ++memberCounter;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.borrowedBookIds = new ArrayList<>();
        this.maxBooksAllowed = 3; // default
        this.memberType = "Regular";
    }

    public String getMemberPrivileges() {
        return "Regular Member: Can borrow up to " + maxBooksAllowed + " books.";
    }

    public boolean canBorrow() {
        return borrowedBookIds.size() < maxBooksAllowed;
    }

    public void borrowBook(int bookId) {
        if (canBorrow()) {
            borrowedBookIds.add(bookId);
        }
    }

    public void returnBook(int bookId) {
        borrowedBookIds.remove(Integer.valueOf(bookId));
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | Type: %s | Borrowed: %d/%d",
                memberId, name, email, memberType, borrowedBookIds.size(), maxBooksAllowed);
    }

    // Getters
    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public ArrayList<Integer> getBorrowedBooks() {
        return borrowedBookIds;
    }

    public int getMaxBooksAllowed() {
        return maxBooksAllowed;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
