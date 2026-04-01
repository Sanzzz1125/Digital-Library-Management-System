package ui;

import java.io.*;

import javax.swing.*;

public class LoginUI {

    public void show() {
        JFrame frame = new JFrame("Login");

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JButton login = new JButton("Login");

        JButton register = new JButton("Register");
        register.setBounds(50, 200, 200, 30);
        frame.add(register);

        user.setBounds(50, 50, 200, 30);
        pass.setBounds(50, 100, 200, 30);
        login.setBounds(50, 150, 200, 30);

        frame.add(user);
        frame.add(pass);
        frame.add(login);

        frame.setSize(300, 300);
        frame.setLayout(null);
        frame.setVisible(true);

        login.addActionListener(e -> {
            String u = user.getText();
            String p = new String(pass.getPassword());

            boolean found = false;

            try {
                BufferedReader br = new BufferedReader(new FileReader("data/users.txt"));
                String line;

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");

                    if (data[0].trim().equals(u.trim()) && data[1].trim().equals(p.trim())) {
                        System.out.println("File user: " + data[0] + " Pass: " + data[1]);
                        System.out.println("Input user: " + u + " Pass: " + p);
                        found = true;
                        break;
                    }
                }

                br.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error reading users file");
            }

            if (u.equals("admin") && p.equals("admin")) {
                frame.setVisible(false);
                new AdminUI(frame).show();
            } else if (found) {
                frame.setVisible(false);
                new DashboardUI(u, frame).show();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Login");
            }
        });

        register.addActionListener(e -> {
            new RegisterUI().show();
        });
    }
}