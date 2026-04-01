package service;

import java.util.*;
import model.Book;
import utils.FileHandler;

public class LibraryService {

    private List<Book> books;
    private final String FILE = "data/books.txt";

    private Map<String, List<String>> borrowedMap = new HashMap<>();

    public LibraryService() {
        books = FileHandler.loadBooks(FILE);
    }

    public List<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
        FileHandler.saveBooks(FILE, books);
    }

    public void borrowBook(int index, String username) throws Exception {
        if (index < 0 || index >= books.size()) {
            throw new Exception("Invalid book index!");
        }
        Book b = books.get(index);

        if (b.getQuantity() <= 0) {
            throw new Exception("Book not available!");
        }

        b.setQuantity(b.getQuantity() - 1);

        borrowedMap.putIfAbsent(b.getTitle(), new ArrayList<>());
        borrowedMap.get(b.getTitle()).add(username);

        FileHandler.saveBooks(FILE, books);
    }

    public void returnBook(int index, String username) {
        if (index < 0 || index >= books.size()) {
            return;
        }
        Book b = books.get(index);

        b.setQuantity(b.getQuantity() + 1);

        if (borrowedMap.containsKey(b.getTitle())) {
            borrowedMap.get(b.getTitle()).remove(username);
        }

        FileHandler.saveBooks(FILE, books);
    }

    public void removeBook(int index) {
        if (index >= 0 && index < books.size()) {
            books.remove(index);
            FileHandler.saveBooks(FILE, books);
        }
        FileHandler.saveBooks(FILE, books);
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> result = new ArrayList<>();

        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(b);
            }
        }

        return result;
    }

    public List<String> getUserBooks(String username) {
        List<String> result = new ArrayList<>();

        for (String book : borrowedMap.keySet()) {
            if (borrowedMap.get(book).contains(username)) {
                result.add(book);
            }
        }

        return result;
    }
}