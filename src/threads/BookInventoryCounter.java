package threads;

public class BookInventoryCounter {
    private int borrowCount = 0;
    private int returnCount = 0;

    public synchronized void incrementBorrow() {
        borrowCount++;
    }

    public synchronized void incrementReturn() {
        returnCount++;
    }

    public synchronized int getBorrowCount() {
        return borrowCount;
    }

    public synchronized int getReturnCount() {
        return returnCount;
    }

    public synchronized String getStats() {
        return String.format("Total Borrows: %d | Total Returns: %d | Net: %d",
                borrowCount, returnCount, borrowCount - returnCount);
    }
}
