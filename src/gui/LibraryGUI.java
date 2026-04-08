package gui;

import main.LibraryCatalog;
import main.LibraryFileManager;
import models.*;
import exceptions.*;
import threads.LibraryThreadManager;
import threads.LibraryNotificationService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LibraryGUI extends JFrame {

    // ─── Data ────────────────────────────────────────────────────────────────
    private LibraryCatalog catalog;

    // ─── UNIT VI: Swing Components ───────────────────────────────────────────
    private JTabbedPane tabbedPane;
    private JTable bookTable;
    private JTable memberTable;
    private DefaultTableModel bookTableModel;
    private DefaultTableModel memberTableModel;
    private JTextArea logArea;
    private JLabel statusBar;
    private JLabel statsLabel;
    private JTree genreTree;

    // Search controls
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JCheckBox availableOnlyCheck;

    // Add Book controls
    private JTextField titleField, authorField, yearField, copiesField;
    private JComboBox<String> genreCombo;
    private JRadioButton rbPremium, rbStudent, rbRegular;

    // Borrow/Return controls
    private JTextField borrowMemberIdField, borrowBookIdField;
    private JTextField returnMemberIdField, returnBookIdField;

    // UNIT V: CardLayout for dynamic panel switching
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Color palette
    private static final Color PRIMARY = new Color(26, 60, 100);
    private static final Color ACCENT = new Color(0, 140, 186);
    private static final Color SUCCESS = new Color(39, 174, 96);
    private static final Color WARNING = new Color(230, 126, 34);
    private static final Color BG_LIGHT = new Color(245, 248, 252);
    private static final Color TEXT_DARK = new Color(30, 40, 60);
    private static final Color CARD_BG = Color.WHITE;

    // ─── Constructor ─────────────────────────────────────────────────────────
    public LibraryGUI() {
        catalog = new LibraryCatalog();

        // Start background thread (UNIT IV)
        LibraryThreadManager.startBackupService(catalog.getBooks());
        LibraryThreadManager.demonstrateThreadLifecycle();

        initUI();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // UNIT IV: Event Handling - Window adapter (Adapter class)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(LibraryGUI.this,
                        "Save data and exit?", "Confirm Exit",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    LibraryFileManager.saveBooks(catalog.getBooks());
                    LibraryFileManager.saveMembers(catalog.getMembers());
                    LibraryThreadManager.stopBackupService();
                    dispose();
                    System.exit(0);
                } else if (choice == JOptionPane.NO_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        setVisible(true);
        appendLog("Digital Library System started. Welcome!");
    }

    // ─── UI Initialization ────────────────────────────────────────────────────
    private void initUI() {
        setTitle("Digital Library Management System");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_LIGHT);

        createMenuBar(); // UNIT V: Menu bar
        createHeader();
        createTabPanel(); // UNIT VI: JTabbedPane
        createStatusBar();

        refreshBookTable();
        refreshMemberTable();
    }

    // ─── UNIT V & VI: MenuBar ─────────────────────────────────────────────────
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY);

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JMenuItem saveItem = createMenuItem("Save Data", "💾", e -> saveAll());
        JMenuItem reportItem = createMenuItem("Generate Report", "📊", e -> generateReport());
        JMenuItem exitItem = createMenuItem("Exit", "🚪", e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.add(reportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setForeground(Color.WHITE);
        toolsMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JMenuItem threadItem = createMenuItem("Thread Info", "🔄", e -> showThreadInfo());
        JMenuItem logItem = createMenuItem("View Full Log", "📋", e -> showFullLog());
        JMenuItem statsItem = createMenuItem("Statistics", "📈", e -> showStats());

        toolsMenu.add(threadItem);
        toolsMenu.add(logItem);
        toolsMenu.add(statsItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JMenuItem aboutItem = createMenuItem("About", "ℹ️",
                e -> JOptionPane.showMessageDialog(this,
                        "Digital Library Management System\n" +
                                "Covers: Java OOP, Inheritance, Exceptions,\n" +
                                "File I/O, Threads, AWT, Swing\n\n" +
                                "Course Project - All 6 Units Covered",
                        "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private JMenuItem createMenuItem(String text, String icon, ActionListener al) {
        JMenuItem item = new JMenuItem(icon + "  " + text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.addActionListener(al);
        return item;
    }

    // ─── Header ───────────────────────────────────────────────────────────────
    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("📚  Digital Library Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statsLabel.setForeground(new Color(180, 210, 240));
        updateStatsLabel();

        header.add(title, BorderLayout.WEST);
        header.add(statsLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    // ─── UNIT VI: JTabbedPane ─────────────────────────────────────────────────
    private void createTabPanel() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BG_LIGHT);

        tabbedPane.addTab("📖 Books", createBooksTab());
        tabbedPane.addTab("👥 Members", createMembersTab());
        tabbedPane.addTab("🔄 Borrow/Return", createBorrowTab());
        tabbedPane.addTab("🔍 Search", createSearchTab());
        tabbedPane.addTab("📁 Genres", createGenreTreeTab());
        tabbedPane.addTab("📋 Activity Log", createLogTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ─── Books Tab ────────────────────────────────────────────────────────────
    private JPanel createBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table - UNIT VI: JScrollPane + JTable
        String[] cols = { "ID", "Title", "Author", "Genre", "Year", "Available", "Total", "Rating" };
        bookTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        bookTable = new JTable(bookTableModel);
        styleTable(bookTable);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 230)));

        // Add Book Form (UNIT V: GridLayout inside BorderLayout)
        JPanel formPanel = createAddBookForm();

        // Buttons - UNIT VI: JButton
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnPanel.setBackground(BG_LIGHT);

        JButton addBtn = createButton("➕ Add Book", SUCCESS);
        JButton removeBtn = createButton("🗑 Remove Book", WARNING);
        JButton refreshBtn = createButton("🔄 Refresh", ACCENT);
        JButton rateBtn = createButton("⭐ Rate Book", new Color(155, 89, 182));

        addBtn.addActionListener(e -> addBook());
        removeBtn.addActionListener(e -> removeSelectedBook());
        refreshBtn.addActionListener(e -> refreshBookTable());
        rateBtn.addActionListener(e -> rateBook());

        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(rateBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.EAST);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAddBookForm() {
        JPanel form = new JPanel();
        form.setLayout(new GridLayout(0, 2, 6, 8)); // UNIT V: GridLayout
        form.setBackground(CARD_BG);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                " Add New Book ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), ACCENT));
        form.setPreferredSize(new Dimension(300, 240));

        titleField = new JTextField();
        authorField = new JTextField();
        yearField = new JTextField("2024");
        copiesField = new JTextField("1");
        genreCombo = new JComboBox<>(new String[] {
                "Technology", "Fiction", "Science", "History",
                "Mathematics", "Dystopian", "Biography", "Poetry"
        });

        addFormRow(form, "Title:", titleField);
        addFormRow(form, "Author:", authorField);
        addFormRow(form, "Genre:", genreCombo);
        addFormRow(form, "Year:", yearField);
        addFormRow(form, "Copies:", copiesField);

        return wrapInPanel(form);
    }

    // ─── Members Tab ──────────────────────────────────────────────────────────
    private JPanel createMembersTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = { "ID", "Name", "Email", "Phone", "Type", "Borrowed", "Max Allowed" };
        memberTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        memberTable = new JTable(memberTableModel);
        styleTable(memberTable);

        JScrollPane scroll = new JScrollPane(memberTable);

        JPanel formPanel = createAddMemberForm();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnPanel.setBackground(BG_LIGHT);

        JButton addMemberBtn = createButton("➕ Add Member", SUCCESS);
        JButton privilegesBtn = createButton("🔑 Show Privileges", ACCENT);
        JButton refreshBtn = createButton("🔄 Refresh", new Color(52, 152, 219));

        addMemberBtn.addActionListener(e -> addMember());
        privilegesBtn.addActionListener(e -> showPrivileges());
        refreshBtn.addActionListener(e -> refreshMemberTable());

        btnPanel.add(addMemberBtn);
        btnPanel.add(privilegesBtn);
        btnPanel.add(refreshBtn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.EAST);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAddMemberForm() {
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 8));
        form.setBackground(CARD_BG);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SUCCESS),
                " Add New Member ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), SUCCESS));
        form.setPreferredSize(new Dimension(300, 220));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        // UNIT VI: JRadioButton + ButtonGroup
        rbPremium = new JRadioButton("Premium");
        rbPremium.setBackground(CARD_BG);
        rbStudent = new JRadioButton("Student");
        rbStudent.setBackground(CARD_BG);
        rbRegular = new JRadioButton("Regular", true);
        rbRegular.setBackground(CARD_BG);

        ButtonGroup grp = new ButtonGroup();
        grp.add(rbPremium);
        grp.add(rbStudent);
        grp.add(rbRegular);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        radioPanel.setBackground(CARD_BG);
        radioPanel.add(rbPremium);
        radioPanel.add(rbStudent);
        radioPanel.add(rbRegular);

        addFormRow(form, "Name:", nameField);
        addFormRow(form, "Email:", emailField);
        addFormRow(form, "Phone:", phoneField);
        form.add(new JLabel("Type:"));
        form.add(radioPanel);

        // Store refs for action
        nameField.setName("addMemberName");
        emailField.setName("addMemberEmail");
        phoneField.setName("addMemberPhone");

        // Keep refs
        form.putClientProperty("nameField", nameField);
        form.putClientProperty("emailField", emailField);
        form.putClientProperty("phoneField", phoneField);

        JPanel wrapper = wrapInPanel(form);
        wrapper.putClientProperty("form", form);
        return wrapper;
    }

    // ─── Borrow/Return Tab ────────────────────────────────────────────────────
    private JPanel createBorrowTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Borrow Card
        JPanel borrowCard = createCard("🔖 Borrow Book", ACCENT);
        borrowMemberIdField = new JTextField();
        borrowBookIdField = new JTextField();
        addFormRow(borrowCard, "Member ID:", borrowMemberIdField);
        addFormRow(borrowCard, "Book ID:", borrowBookIdField);
        JButton borrowBtn = createButton("Borrow Book", ACCENT);
        borrowBtn.addActionListener(e -> borrowBook());
        borrowCard.add(new JLabel());
        borrowCard.add(borrowBtn);

        // Return Card
        JPanel returnCard = createCard("🔁 Return Book", SUCCESS);
        returnMemberIdField = new JTextField();
        returnBookIdField = new JTextField();
        addFormRow(returnCard, "Member ID:", returnMemberIdField);
        addFormRow(returnCard, "Book ID:", returnBookIdField);
        JButton returnBtn = createButton("Return Book", SUCCESS);
        returnBtn.addActionListener(e -> returnBook());
        returnCard.add(new JLabel());
        returnCard.add(returnBtn);

        panel.add(wrapInPanel(borrowCard));
        panel.add(wrapInPanel(returnCard));
        return panel;
    }

    // ─── Search Tab ───────────────────────────────────────────────────────────
    private JPanel createSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Search bar - UNIT V: FlowLayout
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        searchBar.setBackground(CARD_BG);
        searchBar.setBorder(new LineBorder(new Color(200, 215, 230)));

        searchField = new JTextField(20);
        searchTypeCombo = new JComboBox<>(new String[] { "Title", "Author", "Genre", "Rating" });
        availableOnlyCheck = new JCheckBox("Available Only");
        availableOnlyCheck.setBackground(CARD_BG);

        JButton searchBtn = createButton("🔍 Search", PRIMARY);
        JButton clearBtn = createButton("✖ Clear", new Color(150, 150, 160));

        // UNIT IV: KeyListener - search on Enter key
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });

        searchBtn.addActionListener(e -> performSearch());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            refreshBookTable();
        });

        searchBar.add(new JLabel("Search:"));
        searchBar.add(searchField);
        searchBar.add(new JLabel("By:"));
        searchBar.add(searchTypeCombo);
        searchBar.add(availableOnlyCheck);
        searchBar.add(searchBtn);
        searchBar.add(clearBtn);

        // Result table
        String[] cols = { "ID", "Title", "Author", "Genre", "Year", "Available", "Total", "Rating" };
        DefaultTableModel searchModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable searchTable = new JTable(searchModel);
        styleTable(searchTable);

        JScrollPane scroll = new JScrollPane(searchTable);

        // Store searchModel for performSearch
        searchTable.setName("searchResultTable");
        panel.putClientProperty("searchModel", searchModel);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        // Redo search button binding with the correct model
        searchBtn.addActionListener(e2 -> performSearchInto(searchModel));
        clearBtn.addActionListener(e2 -> {
            searchModel.setRowCount(0);
            searchField.setText("");
        });

        return panel;
    }

    // ─── UNIT VI: JTree - Genre Tree Tab ─────────────────────────────────────
    private JPanel createGenreTreeTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("📚 Library Catalog");

        // Group books by genre
        java.util.Map<String, List<Book>> byGenre = new java.util.LinkedHashMap<>();
        for (Book b : catalog.getBooks()) {
            byGenre.computeIfAbsent(b.getGenre(), k -> new java.util.ArrayList<>()).add(b);
        }

        for (java.util.Map.Entry<String, List<Book>> entry : byGenre.entrySet()) {
            DefaultMutableTreeNode genreNode = new DefaultMutableTreeNode(
                    "📂 " + entry.getKey() + " (" + entry.getValue().size() + ")");
            for (Book b : entry.getValue()) {
                genreNode.add(new DefaultMutableTreeNode(
                        "📖 " + b.getTitle() + " — " + b.getAuthor()));
            }
            root.add(genreNode);
        }

        genreTree = new JTree(root);
        genreTree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        genreTree.setRowHeight(24);

        // UNIT IV: Mouse event handling
        genreTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = genreTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        Object node = path.getLastPathComponent();
                        appendLog("Tree node selected: " + node.toString());
                        setStatus("Selected: " + node.toString());
                    }
                }
            }
        });

        // Expand all nodes
        for (int i = 0; i < genreTree.getRowCount(); i++) {
            genreTree.expandRow(i);
        }

        JScrollPane scroll = new JScrollPane(genreTree);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 230)));

        JLabel hint = new JLabel("  Double-click a book node to view details", JLabel.LEFT);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(Color.GRAY);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(hint, BorderLayout.SOUTH);
        return panel;
    }

    // ─── Log Tab ──────────────────────────────────────────────────────────────
    private JPanel createLogTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(20, 30, 45));
        logArea.setForeground(new Color(100, 220, 130));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(logArea);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG_LIGHT);

        JButton clearLogBtn = createButton("🗑 Clear", WARNING);
        JButton loadLogBtn = createButton("📂 Load File Log", ACCENT);
        JButton reportBtn = createButton("📊 Generate Report", SUCCESS);

        clearLogBtn.addActionListener(e -> logArea.setText(""));
        loadLogBtn.addActionListener(e -> loadFileLog());
        reportBtn.addActionListener(e -> generateReport());

        btnPanel.add(clearLogBtn);
        btnPanel.add(loadLogBtn);
        btnPanel.add(reportBtn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ─── Status Bar ───────────────────────────────────────────────────────────
    private void createStatusBar() {
        statusBar = new JLabel("  Ready  |  Digital Library Management System");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(new Color(80, 80, 100));
        statusBar.setBackground(new Color(230, 235, 245));
        statusBar.setOpaque(true);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 230)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        add(statusBar, BorderLayout.SOUTH);
    }

    // ─── Actions ─────────────────────────────────────────────────────────────

    private void addBook() {
        // UNIT III: try-catch-throw
        try {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String genre = (String) genreCombo.getSelectedItem();
            int year = Integer.parseInt(yearField.getText().trim());
            int copies = Integer.parseInt(copiesField.getText().trim());

            if (title.isEmpty() || author.isEmpty())
                throw new InvalidDataException("Title/Author", "empty");

            Book book = new Book(title, author, genre, year, copies);
            catalog.addBook(book);
            refreshBookTable();
            updateStatsLabel();
            appendLog("Book added: " + title + " by " + author);
            setStatus("✅ Book added: " + title);
            clearBookForm();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Year and Copies must be numbers.", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DuplicateBookException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Duplicate", JOptionPane.WARNING_MESSAGE);
        } catch (InvalidDataException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedBook() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book.");
            return;
        }
        int bookId = (int) bookTableModel.getValueAt(row, 0);
        try {
            catalog.removeBook(bookId);
            refreshBookTable();
            updateStatsLabel();
            appendLog("Book removed. ID: " + bookId);
            setStatus("🗑 Book removed.");
        } catch (BookNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rateBook() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a book first.");
            return;
        }
        int bookId = (int) bookTableModel.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "Enter rating (0.0 - 5.0):");
        if (input == null)
            return;
        try {
            double rating = Double.parseDouble(input.trim());
            Book book = catalog.findBookById(bookId);
            book.rateBook(rating);
            refreshBookTable();
            appendLog("Book rated: " + book.getTitle() + " -> " + rating);
            setStatus("⭐ Rated: " + book.getTitle());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid rating.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMember() {
        // Get input via dialog (simplified for stability)
        JTextField n = new JTextField(), em = new JTextField(), ph = new JTextField();
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Name:"));
        p.add(n);
        p.add(new JLabel("Email:"));
        p.add(em);
        p.add(new JLabel("Phone:"));
        p.add(ph);

        int res = JOptionPane.showConfirmDialog(this, p, "Add Member", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION)
            return;

        String name = n.getText().trim(), email = em.getText().trim(), phone = ph.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name required.");
            return;
        }

        Member member;
        if (rbPremium.isSelected())
            member = new PremiumMember(name, email, phone, 99.0);
        else if (rbStudent.isSelected())
            member = new StudentMember(name, email, phone, "STU" + System.currentTimeMillis() % 10000,
                    "Institution", 1);
        else
            member = new Member(name, email, phone);

        catalog.addMember(member);
        refreshMemberTable();
        updateStatsLabel();
        appendLog("Member registered: " + name + " [" + member.getMemberType() + "]");
        setStatus("✅ Member added: " + name);
    }

    private void showPrivileges() {
        StringBuilder sb = new StringBuilder("=== Member Privileges (Dynamic Dispatch) ===\n\n");
        for (Member m : catalog.getMembers()) {
            sb.append(m.getName()).append("\n  → ").append(m.getMemberPrivileges()).append("\n\n");
        }
        showTextDialog("Member Privileges", sb.toString());
    }

    private void borrowBook() {
        try {
            int memberId = Integer.parseInt(borrowMemberIdField.getText().trim());
            int bookId = Integer.parseInt(borrowBookIdField.getText().trim());
            catalog.borrowBook(memberId, bookId);
            refreshBookTable();
            refreshMemberTable();
            appendLog("BORROW: Member " + memberId + " borrowed Book " + bookId);
            setStatus("✅ Book borrowed successfully.");
            borrowMemberIdField.setText("");
            borrowBookIdField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric IDs.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            appendLog("BORROW ERROR: " + e.getMessage());
        }
    }

    private void returnBook() {
        try {
            int memberId = Integer.parseInt(returnMemberIdField.getText().trim());
            int bookId = Integer.parseInt(returnBookIdField.getText().trim());
            catalog.returnBook(memberId, bookId);
            refreshBookTable();
            refreshMemberTable();
            appendLog("RETURN: Member " + memberId + " returned Book " + bookId);
            setStatus("✅ Book returned successfully.");
            returnMemberIdField.setText("");
            returnBookIdField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric IDs.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        // Default: search in book table
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshBookTable();
            return;
        }
        String type = (String) searchTypeCombo.getSelectedItem();
        List<Book> results;
        if ("Rating".equals(type)) {
            try {
                results = catalog.search(Double.parseDouble(keyword));
            } catch (NumberFormatException e) {
                results = java.util.Collections.emptyList();
            }
        } else {
            results = catalog.search(keyword, type.toUpperCase());
        }
        if (availableOnlyCheck.isSelected()) {
            results = results.stream().filter(Book::isAvailable).collect(java.util.stream.Collectors.toList());
        }
        populateBookTable(bookTableModel, results);
        appendLog("Search [" + type + "]: '" + keyword + "' -> " + results.size() + " results");
        setStatus("🔍 Found " + results.size() + " results.");
    }

    private void performSearchInto(DefaultTableModel model) {
        performSearch(); // reuse logic, already populates bookTableModel
    }

    // ─── Table Helpers ────────────────────────────────────────────────────────
    private void refreshBookTable() {
        populateBookTable(bookTableModel, catalog.getBooks());
    }

    private void populateBookTable(DefaultTableModel model, List<Book> books) {
        model.setRowCount(0);
        for (Book b : books) {
            model.addRow(new Object[] {
                    b.getBookId(), b.getTitle(), b.getAuthor(), b.getGenre(),
                    b.getYear(), b.getAvailableCopies(), b.getTotalCopies(),
                    String.format("%.1f", b.getRating())
            });
        }
    }

    private void refreshMemberTable() {
        memberTableModel.setRowCount(0);
        for (Member m : catalog.getMembers()) {
            memberTableModel.addRow(new Object[] {
                    m.getMemberId(), m.getName(), m.getEmail(), m.getPhone(),
                    m.getMemberType(), m.getBorrowedBooks().size(), m.getMaxBooksAllowed()
            });
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(220, 230, 245));
        table.setIntercellSpacing(new Dimension(8, 2));
        table.setShowGrid(true);
    }

    // ─── Utilities ────────────────────────────────────────────────────────────
    private void saveAll() {
        LibraryFileManager.saveBooks(catalog.getBooks());
        LibraryFileManager.saveMembers(catalog.getMembers());
        appendLog("Data saved manually.");
        setStatus("💾 Data saved successfully.");
        JOptionPane.showMessageDialog(this, "Data saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateReport() {
        String report = catalog.generateReport();
        showTextDialog("Library Report", report);
        appendLog("Report generated.");
        setStatus("📊 Report generated.");
    }

    private void showThreadInfo() {
        LibraryThreadManager.printThreadGroupInfo();
        String stats = LibraryThreadManager.inventoryCounter.getStats();
        String info = "Thread Group: LibraryServices\n" +
                "Active Threads: " + Thread.activeCount() + "\n\n" +
                "Inventory Stats:\n" + stats + "\n\n" +
                "Notifications Sent: " +
                LibraryNotificationService.getNotificationCount();
        showTextDialog("Thread Info", info);
        appendLog("Thread info viewed.");
    }

    private void loadFileLog() {
        List<String> entries = LibraryFileManager.readLog();
        if (entries.isEmpty()) {
            appendLog("No log entries found.");
            return;
        }
        logArea.append("\n--- File Log Entries ---\n");
        entries.forEach(e -> logArea.append(e + "\n"));
    }

    private void showStats() {
        int[] stats = LibraryFileManager.readBinaryReport();
        String msg = String.format(
                "Books: %d\nMembers: %d\nTransactions: %d\n(from binary stats file)",
                stats[0], stats[1], stats[2]);
        showTextDialog("Statistics (Binary Data Read)", msg);
    }

    private void showTextDialog(String title, String content) {
        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, sp, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void showFullLog() {
        java.util.List<String> log = LibraryFileManager.readLog();
        StringBuilder sb = new StringBuilder();
        for (String s : log)
            sb.append(s).append("\n");
        showTextDialog("Full Log", sb.toString());
    }

    private void appendLog(String msg) {
        if (logArea != null) {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
        LibraryFileManager.logTransaction(msg);
    }

    private void setStatus(String msg) {
        statusBar.setText("  " + msg);
    }

    private void updateStatsLabel() {
        long avail = catalog.getBooks().stream().filter(Book::isAvailable).count();
        statsLabel.setText(String.format("📖 Books: %d  |  👥 Members: %d  |  ✅ Available: %d  ",
                catalog.getBooks().size(), catalog.getMembers().size(), avail));
    }

    private void clearBookForm() {
        titleField.setText("");
        authorField.setText("");
        yearField.setText("2024");
        copiesField.setText("1");
    }

    // ─── UI Builder Helpers ───────────────────────────────────────────────────
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        // UNIT IV: Mouse events via inner class / adapter
        btn.addMouseListener(new MouseAdapter() {
            Color orig = bg;

            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(orig);
            }
        });
        return btn;
    }

    private JPanel createCard(String title, Color accent) {
        JPanel card = new JPanel(new GridLayout(0, 2, 8, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(accent, 2),
                " " + title + " ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), accent));
        return card;
    }

    private void addFormRow(JPanel panel, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl);
        panel.add(field);
    }

    private JPanel wrapInPanel(JPanel inner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        wrapper.add(inner, BorderLayout.NORTH);
        return wrapper;
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // Set Look and Feel (Swing)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default
        }
        // UNIT VI: Always run Swing on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new LibraryGUI());
    }
}
