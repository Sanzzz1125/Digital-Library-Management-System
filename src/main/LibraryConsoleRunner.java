package main;

import models.*;
import exceptions.*;
import threads.*;
import threads.LibraryNotificationService;
import java.util.*;

public class LibraryConsoleRunner {

    private static LibraryCatalog catalog = new LibraryCatalog();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        LibraryThreadManager.demonstrateThreadLifecycle();

        LibraryThreadManager.startBackupService(catalog.getBooks());

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    listBooks();
                    break;
                case "2":
                    searchBooks();
                    break;
                case "3":
                    addBook();
                    break;
                case "4":
                    borrowBook();
                    break;
                case "5":
                    returnBook();
                    break;
                case "6":
                    listMembers();
                    break;
                case "7":
                    addMember();
                    break;
                case "8":
                    showPrivileges();
                    break;
                case "9":
                    generateReport();
                    break;
                case "10":
                    showThreadInfo();
                    break;
                case "11":
                    demonstrateRecursion();
                    break;
                case "12":
                    demonstrateAbstract();
                    break;
                case "0":
                    LibraryFileManager.saveBooks(catalog.getBooks());
                    LibraryFileManager.saveMembers(catalog.getMembers());
                    LibraryThreadManager.stopBackupService();
                    System.out.println("Goodbye! Data saved.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }

    // ─── Menu ─────────────────────────────────────────────────────────────────
    private static void printMenu() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║    Digital Library Management System       ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.println("║  1. List All Books      6. List Members    ║");
        System.out.println("║  2. Search Books        7. Add Member      ║");
        System.out.println("║  3. Add Book            8. Show Privileges ║");
        System.out.println("║  4. Borrow Book         9. Generate Report ║");
        System.out.println("║  5. Return Book        10. Thread Info     ║");
        System.out.println("║                        11. Recursion Demo  ║");
        System.out.println("║                        12. Abstract Demo   ║");
        System.out.println("║  0. Exit                                   ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.print("  Enter choice: ");
    }

    // ─── 1. List Books ────────────────────────────────────────────────────────
    private static void listBooks() {
        System.out.println("\n── All Books (sorted by title) ──");
        List<Book> sorted = catalog.getSortedBooks(); // uses Collections.sort + Comparable
        for (Book b : sorted) {
            System.out.println(b);
        }
        System.out.println("Total: " + sorted.size() + " books.");
    }

    // ─── 2. Search ────────────────────────────────────────────────────────────
    private static void searchBooks() {
        System.out.println("\n── Search Books ──");
        System.out.println("1. By Title  2. By Author  3. By Genre  4. By Rating");
        System.out.print("Choose: ");
        String type = scanner.nextLine().trim();
        System.out.print("Enter search term: ");
        String term = scanner.nextLine().trim();

        List<Book> results;
        switch (type) {
            case "1":
                results = catalog.searchByTitle(term, true);
                break; // overloaded
            case "2":
                results = catalog.search(term);
                break; // overloaded
            case "3":
                results = catalog.search(term, "GENRE");
                break; // overloaded
            case "4":
                try {
                    results = catalog.search(Double.parseDouble(term)); // overloaded
                } catch (NumberFormatException e) {
                    System.out.println("Invalid rating.");
                    return;
                }
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        System.out.println("Found " + results.size() + " result(s):");
        results.forEach(System.out::println);
    }

    // ─── 3. Add Book ──────────────────────────────────────────────────────────
    private static void addBook() {
        System.out.println("\n── Add New Book ──");
        System.out.print("Title:  ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Genre:  ");
        String genre = scanner.nextLine().trim();
        System.out.print("Year:   ");
        int year = 2024;
        try {
            year = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year, using 2024.");
        }
        System.out.print("Copies: ");
        int copies = 1;
        try {
            copies = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid copies, using 1.");
        }

        Book book = new Book(title, author, genre, year, copies);

        try {
            catalog.addBook(book); // throws DuplicateBookException
            System.out.println("✓ Book added: " + book);
        } catch (DuplicateBookException e) {
            System.out.println("✗ Error [" + e.getErrorCode() + "]: " + e.getMessage());
        }
    }

    // ─── 4. Borrow ────────────────────────────────────────────────────────────
    private static void borrowBook() {
        System.out.println("\n── Borrow a Book ──");
        listMembers();
        listBooks();
        System.out.print("Member ID: ");
        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        System.out.print("Book ID: ");
        int bookId;
        try {
            bookId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        try {
            catalog.borrowBook(memberId, bookId);
            System.out.println("✓ Book borrowed successfully!");
        } catch (BookNotFoundException e) {
            System.out.println("✗ [" + e.getErrorCode() + "] Book not found: " + e.getMessage());
        } catch (MemberNotFoundException e) {
            System.out.println("✗ [" + e.getErrorCode() + "] Member not found: " + e.getMessage());
        } catch (BookNotAvailableException e) {
            System.out.println("✗ [" + e.getErrorCode() + "] Not available: " + e.getMessage());
        } catch (BorrowLimitExceededException e) {
            System.out.println("✗ [" + e.getErrorCode() + "] Limit exceeded: " + e.getMessage());
        }
    }

    // ─── 5. Return ────────────────────────────────────────────────────────────
    private static void returnBook() {
        System.out.println("\n── Return a Book ──");
        System.out.print("Member ID: ");
        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        System.out.print("Book ID: ");
        int bookId;
        try {
            bookId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        try {
            catalog.returnBook(memberId, bookId);
            System.out.println("✓ Book returned successfully!");
        } catch (LibraryException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    // ─── 6. List Members ──────────────────────────────────────────────────────
    private static void listMembers() {
        System.out.println("\n── All Members ──");
        for (Member m : catalog.getMembers()) {
            System.out.println(m);
        }
    }

    // ─── 7. Add Member ────────────────────────────────────────────────────────
    private static void addMember() {
        System.out.println("\n── Add New Member ──");
        System.out.println("Type: 1. Regular  2. Premium  3. Student");
        System.out.print("Choice: ");
        String type = scanner.nextLine().trim();
        System.out.print("Name:   ");
        String name = scanner.nextLine().trim();
        System.out.print("Email:  ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone:  ");
        String phone = scanner.nextLine().trim();

        Member member;
        switch (type) {
            case "2":
                member = new PremiumMember(name, email, phone, 99.0);
                break;
            case "3":
                System.out.print("Student ID: ");
                String sid = scanner.nextLine().trim();
                System.out.print("Institution: ");
                String inst = scanner.nextLine().trim();
                System.out.print("Semester: ");
                int sem = 1;
                try {
                    sem = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                member = new StudentMember(name, email, phone, sid, inst, sem);
                break;
            default:
                member = new Member(name, email, phone);
                break;
        }
        catalog.addMember(member);
        System.out.println("✓ Member added: " + member);
    }

    // ─── 8. Privileges ────────────────────────────────────────────────────────
    private static void showPrivileges() {
        System.out.println("\n── Member Privileges (Dynamic Method Dispatch) ──");
        for (Member m : catalog.getMembers()) {
            // The JVM calls the actual overridden method at runtime
            System.out.println(m.getName() + " [" + m.getMemberType() + "]");
            System.out.println("  → " + m.getMemberPrivileges());
        }
    }

    // ─── 9. Report ────────────────────────────────────────────────────────────
    private static void generateReport() {
        System.out.println("\n── Generating Report ──");
        String report = catalog.generateReport();
        System.out.println(report);
        int[] binStats = LibraryFileManager.readBinaryReport();
        System.out.println("\n-- Binary Stats (DataInputStream) --");
        System.out.println("Books: " + binStats[0] + " | Members: " + binStats[1]
                + " | Transactions: " + binStats[2]);

        System.out.println("\n-- Last 5 Log Entries (FileReader + Scanner) --");
        List<String> log = LibraryFileManager.readLog();
        int start = Math.max(0, log.size() - 5);
        for (int i = start; i < log.size(); i++) {
            System.out.println(log.get(i));
        }
    }

    // ─── 10. Thread Info ──────────────────────────────────────────────────────
    private static void showThreadInfo() {
        LibraryThreadManager.printThreadGroupInfo();
        System.out.println("\n── Synchronized Inventory Counter ──");
        System.out.println(LibraryThreadManager.inventoryCounter.getStats());
        System.out.println("Notifications sent: "
                + LibraryNotificationService.getNotificationCount());
    }

    // ─── 11. Recursion Demo ───────────────────────────────────────────────────
    private static void demonstrateRecursion() {
        System.out.println("\n── Recursion Demo: Binary Search on Sorted Books ──");
        System.out.print("Enter title to search (recursive binary search): ");
        String title = scanner.nextLine().trim();
        List<Book> sorted = catalog.getSortedBooks();
        Book found = catalog.recursiveTitleSearch(title, 0, sorted.size() - 1, sorted);
        if (found != null) {
            System.out.println("✓ Found: " + found);
        } else {
            System.out.println("✗ Not found (partial title may not work — exact match needed).");
        }

        // Also demonstrate factorial recursion
        System.out.println("\n── Factorial (Recursion Example) ──");
        for (int i = 1; i <= 10; i++) {
            System.out.printf("  %2d! = %d%n", i, factorial(i));
        }
    }

    private static long factorial(int n) {
        if (n <= 1)
            return 1; // base case
        return n * factorial(n - 1); // recursive call
    }

    // ─── 12. Abstract Class Demo ─────────────────────────────────────────────
    private static void demonstrateAbstract() {
        System.out.println("\n── Abstract Classes Demo ──");
        LibraryReport[] reports = {
                new AvailabilityReport(),
                new GenreReport()
        };
        for (LibraryReport report : reports) {
            // Abstract method called — actual subclass method runs (Dynamic Dispatch)
            report.printReport(catalog.getBooks());
            System.out.println();
        }
    }
}
