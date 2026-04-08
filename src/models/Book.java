package models;

import java.io.Serializable;

/**
 * UNIT I: Classes, Objects, Constructors, Access Specifiers, Methods
 * Represents a Book in the Digital Library
 */
public class Book implements Serializable, Comparable<Book> {

    // UNIT I: Data types, variables, access specifiers
    private static final long serialVersionUID = 1L;
    private static int idCounter = 1000;

    private int bookId;
    private String title;
    private String author;
    private String genre;
    private int year;
    private boolean isAvailable;
    private int totalCopies;
    private int availableCopies;
    private double rating;

    // UNIT I: Default Constructor
    public Book() {
        this.bookId = ++idCounter;
        this.isAvailable = true;
        this.totalCopies = 1;
        this.availableCopies = 1;
        this.rating = 0.0;
    }

    // UNIT I: Parameterized Constructor
    public Book(String title, String author, String genre, int year, int copies) {
        this();
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.totalCopies = copies;
        this.availableCopies = copies;
        this.isAvailable = copies > 0;
    }

    // UNIT I: Methods
    public boolean borrowBook() {
        if (availableCopies > 0) {
            availableCopies--;
            isAvailable = availableCopies > 0;
            return true;
        }
        return false;
    }

    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            isAvailable = true;
        }
    }

    public void rateBook(double newRating) {
        if (newRating >= 0 && newRating <= 5) {
            this.rating = (this.rating + newRating) / 2.0;
        }
    }

    // UNIT II: Comparable interface - compareTo method for sorting
    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public String toString() {
        return String.format("[%d] \"%s\" by %s (%d) | Genre: %s | Available: %d/%d | Rating: %.1f",
                bookId, title, author, year, genre, availableCopies, totalCopies, rating);
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public double getRating() {
        return rating;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
