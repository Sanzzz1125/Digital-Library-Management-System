package ui;

import javax.swing.*;

public class DashboardUI {

    String currentUser;
    JFrame loginFrame;

    public DashboardUI(String user, JFrame loginFrame) {
        this.currentUser = user;
        this.loginFrame = loginFrame;
    }

    public void show() {
        JFrame frame = new JFrame("Dashboard");

        JButton logout = new JButton("Logout");
        logout.setBounds(50, 120, 200, 40);
        frame.add(logout);

        JButton viewBooks = new JButton("View Books");

        logout.addActionListener(e -> {
            frame.dispose(); // close dashboard
            loginFrame.setVisible(true); // show login again
        });

        viewBooks.setBounds(50, 50, 200, 40);
        frame.add(viewBooks);

        viewBooks.addActionListener(e -> new BookUI(currentUser).show());

        frame.setSize(300, 200);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}