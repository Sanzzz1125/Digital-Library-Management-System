package ui;

import javax.swing.*;
import java.io.*;

public class RegisterUI {

    public void show() {
        JFrame frame = new JFrame("Register");

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JButton register = new JButton("Register");

        user.setBounds(50, 50, 200, 30);
        pass.setBounds(50, 100, 200, 30);
        register.setBounds(50, 150, 200, 30);

        frame.add(user);
        frame.add(pass);
        frame.add(register);

        register.addActionListener(e -> {
            String username = user.getText().trim();
            String password = new String(pass.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Fill all fields!");
                return;
            }

            try {
                File file = new File("data/users.txt");
                boolean addNewLine = file.exists() && file.length() > 0;
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                if (addNewLine) {
                    bw.newLine();
                }
                bw.write(username + "," + password);
                bw.close();
                JOptionPane.showMessageDialog(frame, "Registered!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        frame.setSize(300, 300);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}