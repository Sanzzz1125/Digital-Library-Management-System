package threads;

import models.Book;
import java.util.List;

class LibraryBackupThread extends Thread {

    private List<Book> books;
    private volatile boolean running = true;

    public LibraryBackupThread(List<Book> books) {
        super("BackupThread");
        this.books = books;
        setDaemon(true);
    }

    @Override
    public void run() {
        System.out.println("[BackupThread] Started. (Daemon: " + isDaemon() + ")");
        while (running) {
            try {
                Thread.sleep(30000);
                System.out.println("[BackupThread] Auto-backup triggered... Books: " + books.size());
                main.LibraryFileManager.saveBooks(books);
            } catch (InterruptedException e) {
                System.out.println("[BackupThread] Interrupted - stopping.");
                running = false;
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[BackupThread] Stopped.");
    }

    public void stopBackup() {
        running = false;
        interrupt();
    }
}

public class LibraryThreadManager {

    private static final ThreadGroup libraryGroup = new ThreadGroup("LibraryServices");
    private static LibraryBackupThread backupThread;
    public static final BookInventoryCounter inventoryCounter = new BookInventoryCounter();

    public static void startBackupService(List<Book> books) {
        backupThread = new LibraryBackupThread(books);
        backupThread.start();
    }

    public static void stopBackupService() {
        if (backupThread != null && backupThread.isAlive()) {
            backupThread.stopBackup();
        }
    }

    public static void sendNotification(String member, String msg) {
        Thread t = new Thread(libraryGroup,
                new LibraryNotificationService(member, msg),
                "NotificationThread-" + System.currentTimeMillis());
        t.start();
    }

    public static void printThreadGroupInfo() {
        System.out.println("\n[ThreadGroup] Name: " + libraryGroup.getName());
        System.out.println("[ThreadGroup] Active threads: " + libraryGroup.activeCount());
        Thread[] threads = new Thread[libraryGroup.activeCount()];
        libraryGroup.enumerate(threads);
        for (Thread t : threads) {
            if (t != null) {
                System.out.println("  -> " + t.getName() + " | State: " + t.getState());
            }
        }
    }

    public static void demonstrateThreadLifecycle() {
        System.out.println("\n=== Thread Lifecycle Demo ===");

        Thread demo = new Thread(() -> {
            System.out.println("[Demo] Thread RUNNING");
            try {
                Thread.sleep(1000);
                System.out.println("[Demo] Thread resumed after sleep");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "LifecycleDemo");

        System.out.println("[Demo] State before start: " + demo.getState()); // NEW
        demo.start();
        System.out.println("[Demo] State after start:  " + demo.getState()); // RUNNABLE
        try {
            demo.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[Demo] State after join:   " + demo.getState()); // TERMINATED
    }
}
