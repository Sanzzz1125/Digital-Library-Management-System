package main;

import models.*;
import exceptions.*;
import threads.*;
import java.util.*;
import java.util.stream.*;

public class LibraryCatalog {

    private List<Book> books;
    private List<Member> members;
    private int transactionCount;

    public LibraryCatalog() {
        this.books = LibraryFileManager.loadBooks();
        this.members = LibraryFileManager.loadMembers();
        this.transactionCount = 0;

        if (books.isEmpty()) {
            seedDefaultBooks();
        }
        if (members.isEmpty()) {
            seedDefaultMembers();
        }
    }

    public void addBook(Book book) throws DuplicateBookException {
        try {
            boolean exists = books.stream()
                    .anyMatch(b -> b.getTitle().equalsIgnoreCase(book.getTitle())
                            && b.getAuthor().equalsIgnoreCase(book.getAuthor()));
            if (exists) {
                throw new DuplicateBookException(book.getTitle());
            }
            books.add(book);
            LibraryFileManager.logTransaction("ADDED book: " + book.getTitle());
            LibraryFileManager.saveBooks(books);
        } catch (DuplicateBookException e) {
            throw e;
        } finally {
            System.out.println("[Catalog] addBook() completed.");
        }
    }

    public boolean removeBook(int bookId) throws BookNotFoundException {
        Book book = findBookById(bookId);
        books.remove(book);
        LibraryFileManager.logTransaction("REMOVED book ID: " + bookId);
        LibraryFileManager.saveBooks(books);
        return true;
    }

    public Book searchByTitle(String title) {
        return books.stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .findFirst().orElse(null);
    }

    public List<Book> searchByTitle(String title, boolean partialMatch) {
        String lower = title.toLowerCase();
        return books.stream()
                .filter(b -> partialMatch
                        ? b.getTitle().toLowerCase().contains(lower)
                        : b.getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());
    }

    public List<Book> search(String author) {
        return books.stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> search(String keyword, String field) {
        String lower = keyword.toLowerCase();
        return books.stream().filter(b -> {
            switch (field.toUpperCase()) {
                case "AUTHOR":
                    return b.getAuthor().toLowerCase().contains(lower);
                case "GENRE":
                    return b.getGenre().toLowerCase().contains(lower);
                case "TITLE":
                    return b.getTitle().toLowerCase().contains(lower);
                default:
                    return b.toString().toLowerCase().contains(lower);
            }
        }).collect(Collectors.toList());
    }

    public List<Book> search(double minRating) {
        return books.stream()
                .filter(b -> b.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    public void borrowBook(int memberId, int bookId)
            throws BookNotFoundException, MemberNotFoundException,
            BookNotAvailableException, BorrowLimitExceededException {
        try {
            Member member = findMemberById(memberId);
            Book book = findBookById(bookId);

            if (!member.canBorrow()) {
                throw new BorrowLimitExceededException(member.getName(), member.getMaxBooksAllowed());
            }
            if (!book.isAvailable()) {
                throw new BookNotAvailableException(book.getTitle());
            }

            book.borrowBook();
            member.borrowBook(bookId);

            threads.LibraryThreadManager.inventoryCounter.incrementBorrow();
            threads.LibraryThreadManager.sendNotification(member.getName(),
                    "You borrowed: " + book.getTitle());
            LibraryFileManager.logTransaction(
                    "BORROW - Member:" + member.getName() + " Book:" + book.getTitle());
            transactionCount++;

            LibraryFileManager.saveBooks(books);
            LibraryFileManager.saveMembers(members);

        } catch (BookNotFoundException | MemberNotFoundException | BookNotAvailableException
                | BorrowLimitExceededException e) {
            System.err.println("[Error] " + e.getMessage());
            throw e;
        }
    }

    public void returnBook(int memberId, int bookId)
            throws BookNotFoundException, MemberNotFoundException {
        Member member = findMemberById(memberId);
        Book book = findBookById(bookId);

        book.returnBook();
        member.returnBook(bookId);

        LibraryThreadManager.inventoryCounter.incrementReturn();
        threads.LibraryThreadManager.sendNotification(member.getName(),
                "You returned: " + book.getTitle() + ". Thank you!");
        LibraryFileManager.logTransaction(
                "RETURN - Member:" + member.getName() + " Book:" + book.getTitle());
        transactionCount++;

        LibraryFileManager.saveBooks(books);
        LibraryFileManager.saveMembers(members);
    }

    public void addMember(Member member) {
        members.add(member);
        LibraryFileManager.logTransaction("REGISTERED member: " + member.getName());
        LibraryFileManager.saveMembers(members);
    }

    public Member findMemberById(int id) throws MemberNotFoundException {
        return members.stream()
                .filter(m -> m.getMemberId() == id)
                .findFirst()
                .orElseThrow(() -> new MemberNotFoundException(id));
    }

    public Book findBookById(int id) throws BookNotFoundException {
        return books.stream()
                .filter(b -> b.getBookId() == id)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book recursiveTitleSearch(String title, int low, int high, List<Book> sorted) {
        if (low > high)
            return null;
        int mid = (low + high) / 2;
        int cmp = sorted.get(mid).getTitle().compareToIgnoreCase(title);
        if (cmp == 0)
            return sorted.get(mid);
        else if (cmp > 0)
            return recursiveTitleSearch(title, low, mid - 1, sorted);
        else
            return recursiveTitleSearch(title, mid + 1, high, sorted);
    }

    public String generateReport() {
        String report = LibraryFileManager.generateInMemoryReport(books, members);
        LibraryFileManager.saveTextReport(report);
        LibraryFileManager.writeBinaryReport(books.size(), members.size(), transactionCount);
        return report;
    }

    public void printAllMemberPrivileges() {
        System.out.println("\n=== Member Privileges (Dynamic Method Dispatch) ===");
        for (Member m : members) {
            System.out.println(m.getName() + ": " + m.getMemberPrivileges());
        }
    }

    public List<Book> getSortedBooks() {
        List<Book> sorted = new ArrayList<>(books);
        Collections.sort(sorted);
        return sorted;
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Member> getMembers() {
        return members;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    private void seedDefaultBooks() {
        String[][] data = {
                { "Clean Code", "Robert C. Martin", "Technology", "2008", "3" },
                { "The Pragmatic Programmer", "David Thomas", "Technology", "2019", "2" },
                { "Design Patterns", "Gang of Four", "Technology", "1994", "2" },
                { "Head First Java", "Kathy Sierra", "Technology", "2005", "4" },
                { "Effective Java", "Joshua Bloch", "Technology", "2018", "3" },
                { "Introduction to Algorithms", "CLRS", "Mathematics", "2009", "2" },
                { "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", "1925", "5" },
                { "To Kill a Mockingbird", "Harper Lee", "Fiction", "1960", "4" },
                { "1984", "George Orwell", "Dystopian", "1949", "3" },
                { "Sapiens", "Yuval Noah Harari", "History", "2011", "4" },
                { "A Brief History of Time", "Stephen Hawking", "Science", "1988", "2" },
                { "The Alchemist", "Paulo Coelho", "Fiction", "1988", "5" },
        };
        for (String[] d : data) {
            books.add(new Book(d[0], d[1], d[2], Integer.parseInt(d[3]), Integer.parseInt(d[4])));
        }
        books.get(0).rateBook(4.8);
        books.get(1).rateBook(4.5);
        books.get(3).rateBook(4.7);
        books.get(6).rateBook(4.2);
        LibraryFileManager.saveBooks(books);
    }

    private void seedDefaultMembers() {
        members.add(new PremiumMember("Alice Johnson", "alice@email.com", "9876543210", 99.0));
        members.add(new StudentMember("Bob Smith", "bob@uni.edu", "9123456780",
                "STU2024001", "MIT", 4));
        members.add(new Member("Carol White", "carol@email.com", "9000000001"));
        members.add(new StudentMember("David Lee", "david@college.edu", "9000000002",
                "STU2024002", "IIT", 6));
        LibraryFileManager.saveMembers(members);
    }
}
