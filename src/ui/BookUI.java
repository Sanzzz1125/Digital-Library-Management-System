package ui;

import javax.swing.*;
import service.LibraryService;
import model.Book;
import java.util.*;

public class BookUI {
    String currentUser;

    public BookUI(String user) {
        this.currentUser = user;
    }

    LibraryService service = new LibraryService();
    JTable table;
    JTextField searchField;

    public void show() {
        JFrame frame = new JFrame("Books");

        JLabel titleLabel = new JLabel("Digital Library System", SwingConstants.CENTER);
        titleLabel.setBounds(100, 5, 300, 30);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        frame.add(titleLabel);

        searchField = new JTextField();
        JButton searchBtn = new JButton("Search");

        searchField.setBounds(20, 40, 200, 30);
        searchBtn.setBounds(230, 40, 100, 30);

        frame.add(searchField);
        frame.add(searchBtn);

        table = new JTable();
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 80, 400, 200);
        frame.add(sp);

        JButton borrow = new JButton("Borrow");
        borrow.setBounds(150, 260, 100, 30);
        frame.add(borrow);

        JButton returnBtn = new JButton("Return");
        returnBtn.setBounds(270, 260, 100, 30);
        frame.add(returnBtn);

        JButton myBooks = new JButton("My Books");
        myBooks.setBounds(20, 260, 100, 30);
        frame.add(myBooks);

        JButton logout = new JButton("Logout");
        logout.setBounds(380, 10, 90, 25);
        frame.add(logout);

        loadTable(service.getBooks());

        searchBtn.addActionListener(e -> {
            loadTable(service.searchBooks(searchField.getText()));
        });

        logout.addActionListener(e -> {
            frame.dispose();
        });

        borrow.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a book first!");
                return;
            }

            try {
                service.borrowBook(row, currentUser);
                JOptionPane.showMessageDialog(frame, "Borrowed!");
                loadTable(service.getBooks());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        returnBtn.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a book first!");
                return;
            }

            service.returnBook(row, currentUser);
            JOptionPane.showMessageDialog(frame, "Returned!");
            loadTable(service.getBooks());
        });

        myBooks.addActionListener(e -> {
            List<String> books = service.getUserBooks(currentUser);

            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No books borrowed");
            } else {
                JOptionPane.showMessageDialog(frame, String.join("\n", books));
            }
        });

        frame.setSize(480, 350);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private void loadTable(List<Book> books) {
        String[] col = { "Title", "Author", "Quantity" };
        String[][] data = new String[books.size()][3];

        for (int i = 0; i < books.size(); i++) {
            data[i][0] = books.get(i).getTitle();
            data[i][1] = books.get(i).getAuthor();
            data[i][2] = String.valueOf(books.get(i).getQuantity());
        }

        table.setModel(new javax.swing.table.DefaultTableModel(data, col));
    }
}