package threads;

public class LibraryNotificationService implements Runnable {

    private String memberName;
    private String message;
    private static int notificationCount = 0;

    public LibraryNotificationService(String memberName, String message) {
        this.memberName = memberName;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500); // simulate network delay
            synchronized (LibraryNotificationService.class) {
                notificationCount++;
                System.out.printf("[Notification #%d] To: %s | %s%n",
                        notificationCount, memberName, message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static int getNotificationCount() {
        return notificationCount;
    }
}
