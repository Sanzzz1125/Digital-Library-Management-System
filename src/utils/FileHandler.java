package utils;

import java.io.*;
import java.util.*;
import model.Book;

public class FileHandler {

    public static List<Book> loadBooks(String path) {
        List<Book> books = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                books.add(new Book(data[0], data[1], Integer.parseInt(data[2])));
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading books");
        }
        return books;
    }

    public static void saveBooks(String path, List<Book> books) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            for (Book b : books) {
                bw.write(b.toString());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Error saving books");
        }
    }
}