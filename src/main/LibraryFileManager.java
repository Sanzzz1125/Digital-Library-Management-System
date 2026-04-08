package main;

import models.Book;
import models.Member;

import java.io.*;
import java.util.*;

public class LibraryFileManager {

    private static final String BOOKS_FILE = "data/books.dat";
    private static final String MEMBERS_FILE = "data/members.dat";
    private static final String LOG_FILE = "data/library_log.txt";
    private static final String REPORT_FILE = "data/library_report.txt";

    @SuppressWarnings("unchecked")
    public static void saveBooks(List<Book> books) {
        new File("data").mkdirs();
        try (FileOutputStream fos = new FileOutputStream(BOOKS_FILE);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(books);
            System.out.println("[File] Books saved successfully.");
        } catch (IOException e) {
            System.err.println("[File Error] Could not save books: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Book> loadBooks() {
        File f = new File(BOOKS_FILE);
        if (!f.exists())
            return new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(BOOKS_FILE);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            List<Book> books = (List<Book>) ois.readObject();
            System.out.println("[File] Loaded " + books.size() + " books.");
            return books;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[File Error] Could not load books: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void updateMemberName(int memberId, String newName) {
        List<Member> members = loadMembers();
        boolean found = false;

        for (Member m : members) {
            if (m.getId() == memberId) {
                m.setName(newName);
                found = true;
                break;
            }
        }

        if (found) {
            saveMembers(members);
            System.out.println("[File] Member updated successfully.");
        } else {
            System.out.println("[File] Member not found.");
        }
    }

    public static void saveMembers(List<Member> members) {
        new File("data").mkdirs();
        try (FileOutputStream fos = new FileOutputStream(MEMBERS_FILE);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(members);
            System.out.println("[File] Members saved successfully.");
        } catch (IOException e) {
            System.err.println("[File Error] Could not save members: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Member> loadMembers() {
        File f = new File(MEMBERS_FILE);
        if (!f.exists())
            return new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(MEMBERS_FILE);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            List<Member> members = (List<Member>) ois.readObject();
            System.out.println("[File] Loaded " + members.size() + " members.");
            return members;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[File Error] Could not load members: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void logTransaction(String message) {
        new File("data").mkdirs();
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new java.util.Date());
            bw.write("[" + timestamp + "] " + message);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("[Log Error] " + e.getMessage());
        }
    }

    public static List<String> readLog() {
        List<String> entries = new ArrayList<>();
        File f = new File(LOG_FILE);
        if (!f.exists())
            return entries;
        try (FileReader fr = new FileReader(LOG_FILE);
                Scanner scanner = new Scanner(fr)) {
            while (scanner.hasNextLine()) {
                entries.add(scanner.nextLine());
            }
        } catch (IOException e) {
            System.err.println("[Log Error] " + e.getMessage());
        }
        return entries;
    }

    public static void writeBinaryReport(int totalBooks, int totalMembers, int transactions) {
        new File("data").mkdirs();
        try (FileOutputStream fos = new FileOutputStream("data/stats.bin");
                DataOutputStream dos = new DataOutputStream(fos)) {
            dos.writeInt(totalBooks);
            dos.writeInt(totalMembers);
            dos.writeInt(transactions);
            dos.writeDouble(System.currentTimeMillis());
            System.out.println("[File] Binary stats written.");
        } catch (IOException e) {
            System.err.println("[Binary Error] " + e.getMessage());
        }
    }

    public static int[] readBinaryReport() {
        File f = new File("data/stats.bin");
        if (!f.exists())
            return new int[] { 0, 0, 0 };
        try (FileInputStream fis = new FileInputStream("data/stats.bin");
                DataInputStream dis = new DataInputStream(fis)) {
            int books = dis.readInt();
            int members = dis.readInt();
            int trans = dis.readInt();
            return new int[] { books, members, trans };
        } catch (IOException e) {
            System.err.println("[Binary Read Error] " + e.getMessage());
            return new int[] { 0, 0, 0 };
        }
    }

    public static String generateInMemoryReport(List<Book> books, List<Member> members) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        ps.println("=== LIBRARY REPORT ===");
        ps.println("Total Books: " + books.size());
        ps.println("Total Members: " + members.size());

        long available = books.stream().filter(Book::isAvailable).count();
        ps.println("Available Books: " + available);
        ps.println("Borrowed Books: " + (books.size() - available));

        CharArrayWriter caw = new CharArrayWriter();

        try {
            caw.write("\n--- Book List ---\n");

            for (Book b : books) {
                caw.write(b.toString() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace(); // or ignore
        }

        return baos.toString() + caw.toString();
    }

    public static void saveTextReport(String content) {
        new File("data").mkdirs();
        try (FileWriter fw = new FileWriter(REPORT_FILE)) {
            fw.write(content);
            System.out.println("[File] Report saved to " + REPORT_FILE);
        } catch (IOException e) {
            System.err.println("[Report Error] " + e.getMessage());
        }
    }
}
