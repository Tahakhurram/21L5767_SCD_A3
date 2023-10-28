package test;

import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

class Item {
    private static int nextId = 1;

    protected int id;
    protected String title;
    protected int type;

    public Item(String title, int type) {
        this.id = nextId++;
        this.title = title;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }
}

class Book extends Item {
    private String author;
    private int pageCount;
    private int year;

    public Book(String title, String author, int type, int pageCount, int year) {
        super(title, type);
        this.author = author;
        this.pageCount = pageCount;
        this.year = year;
    }

    public String getAuthor() {
        return author;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getYear() {
        return year;
    }
}

class Library {
    private List<Item> items;

    public Library() {
        items = new ArrayList<>();
    }

    public boolean addItem(Item item) {
        return items.add(item);
    }

    public boolean deleteItem(String title) {
        for (Item item : items) {
            if (item.getTitle().equals(title)) {
                return items.remove(item);
            }
        }
        return false;
    }

    public List<Item> getAvailableItems() {
        return items;
    }
    
    public List<Item> getItems() {
        return items;
    }
}

class FileHandler {
    public List<Item> loadItemsFromFile(String filename) {
        List<Item> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0].trim());
                    String title = parts[1].trim();
                    int type = Integer.parseInt(parts[2].trim());
                    Item item = new Item(title, type);
                    items.add(item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading items from file: " + e.getMessage());
        }
        return items;
    }

    public void saveItemsToFile(List<Item> items, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Item item : items) {
                String line = item.getId() + ", " + item.getTitle() + ", " + item.getType();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving items to file: " + e.getMessage());
        }
    }
}

class LibraryManagementSystemGUI {
    private Library library;
    private DefaultTableModel tableModel;
    private JFrame frame;
    private JTable table;

    public LibraryManagementSystemGUI() {
        library = new Library();
        FileHandler fileHandler = new FileHandler();
        List<Item> items = fileHandler.loadItemsFromFile("items.txt");
        library.getItems().addAll(items);

        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Item");
        JButton editButton = new JButton("Edit Item");
        JButton deleteButton = new JButton("Delete Item");
        JButton viewPopularityButton = new JButton("View Popularity");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewPopularityButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddItemDialog();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //edit item
                
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteItemDialog();
            }
        });

        viewPopularityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPopularityChart();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void showDeleteItemDialog() {
        String itemName = JOptionPane.showInputDialog(frame, "Enter the title of the item to delete:");
        if (itemName != null && !itemName.isEmpty()) {
            boolean itemDeleted = library.deleteItem(itemName);
            if (itemDeleted) {
                JOptionPane.showMessageDialog(frame, "Item deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            updateTable();
        }
    }

    private void showAddItemDialog() {
        JFrame addItemFrame = new JFrame("Add Item");
        addItemFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addItemFrame.setSize(300, 200);
        addItemFrame.setLayout(new GridLayout(4, 2));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();
        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            
                    @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String author = authorField.getText();
                if (!title.isEmpty() && !author.isEmpty()) {
                    boolean itemAdded = library.addItem(new Book(title, author, 0, 0, 0));
                    if (itemAdded) {
                        JOptionPane.showMessageDialog(frame, "Item added successfully!");
                        updateTable();
                        addItemFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to add item.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Title and author are required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addItemFrame.add(titleLabel);
        addItemFrame.add(titleField);
        addItemFrame.add(authorLabel);
        addItemFrame.add(authorField);
        addItemFrame.add(addButton);

        addItemFrame.setVisible(true);
    }

    private void showPopularityChart() {
        JFrame popularityFrame = new JFrame("Popularity Chart");
        popularityFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popularityFrame.setLayout(new BorderLayout());

        
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
            }
        };
        popularityFrame.add(chartPanel, BorderLayout.CENTER);

        popularityFrame.setSize(400, 300);
        popularityFrame.setLocationRelativeTo(null); 
        popularityFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    private void updateTable() {
        tableModel.setRowCount(0);
        List<Item> items = library.getAvailableItems();
        for (Item item : items) {
            if (item instanceof Book) {
                Book book = (Book) item;
                Object[] row = { book.getId(), book.getTitle(), book.getAuthor(), book.getPageCount(), book.getYear() };
                tableModel.addRow(row);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryManagementSystemGUI();
        });
    }
}
