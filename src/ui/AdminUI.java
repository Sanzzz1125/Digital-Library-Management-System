package ui;

import javax.swing.*;
import service.LibraryService;
import model.Book;

public class AdminUI {

    LibraryService service = new LibraryService();
    JFrame loginFrame;

    public AdminUI(JFrame loginFrame) {
        this.loginFrame = loginFrame;
    }

    public void show() {
        JFrame frame = new JFrame("Admin Panel");

        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField qty = new JTextField();

        JButton logout = new JButton("Logout");
        logout.setBounds(50, 300, 200, 30);
        frame.add(logout);

        JButton add = new JButton("Add Book");
        JButton remove = new JButton("Remove Book");

        title.setBounds(50, 50, 200, 30);
        author.setBounds(50, 100, 200, 30);
        qty.setBounds(50, 150, 200, 30);

        add.setBounds(50, 200, 200, 30);
        remove.setBounds(50, 250, 200, 30);

        frame.add(title);
        frame.add(author);
        frame.add(qty);
        frame.add(add);
        frame.add(remove);

        logout.addActionListener(e -> {
            frame.dispose();
            loginFrame.setVisible(true);
        });

        add.addActionListener(e -> {
            if (title.getText().isEmpty() || author.getText().isEmpty() || qty.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Fill all fields!");
                return;
            }

            try {
                int quantity = Integer.parseInt(qty.getText());
                service.addBook(new Book(title.getText(), author.getText(), quantity));
                JOptionPane.showMessageDialog(frame, "Book Added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Enter valid quantity!");
            }
        });

        remove.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter index to remove:");
            int index = Integer.parseInt(input);

            service.removeBook(index);
            JOptionPane.showMessageDialog(frame, "Book Removed!");
        });

        frame.setSize(300, 350);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}