package main;

import models.Book;
import java.util.List;

/**
 * UNIT II: Abstract Classes
 * Demonstrates: abstract class, abstract methods, concrete methods
 */
public abstract class LibraryReport {

    // Abstract methods - must be implemented by subclasses
    public abstract String getReportTitle();
    public abstract String generateContent(List<Book> books);

    // Concrete method - shared implementation
    public final void printReport(List<Book> books) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  " + getReportTitle());
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println(generateContent(books));
        System.out.println("Generated: " + new java.util.Date());
    }
}

// Concrete subclass 1
class AvailabilityReport extends LibraryReport {
    @Override
    public String getReportTitle() {
        return "BOOK AVAILABILITY REPORT";
    }

    @Override
    public String generateContent(List<Book> books) {
        StringBuilder sb = new StringBuilder();
        long available = books.stream().filter(Book::isAvailable).count();
        sb.append("Total Books:     ").append(books.size()).append("\n");
        sb.append("Available:       ").append(available).append("\n");
        sb.append("Not Available:   ").append(books.size() - available).append("\n");
        return sb.toString();
    }
}

// Concrete subclass 2
class GenreReport extends LibraryReport {
    @Override
    public String getReportTitle() {
        return "GENRE DISTRIBUTION REPORT";
    }

    @Override
    public String generateContent(List<Book> books) {
        java.util.Map<String, Long> byGenre = new java.util.LinkedHashMap<>();
        for (Book b : books) {
            byGenre.merge(b.getGenre(), 1L, Long::sum);
        }
        StringBuilder sb = new StringBuilder();
        byGenre.forEach((genre, count) ->
            sb.append(String.format("  %-15s : %d\n", genre, count)));
        return sb.toString();
    }
}
