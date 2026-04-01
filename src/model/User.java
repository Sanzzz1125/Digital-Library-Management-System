package model;

import java.util.*;

public class User {
    protected String username;
    protected String password;
    protected List<String> borrowedBooks = new ArrayList<>();

    public User(String u, String p) {
        username = u;
        password = p;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getBorrowedBooks() {
        return borrowedBooks;
    }
}