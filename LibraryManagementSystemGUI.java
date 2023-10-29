package test;

import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private int popularityCount;

    public Book(String title, String author, int type, int pageCount, int year) {
        super(title, type);
        this.author = author;
        this.pageCount = pageCount;
        this.year = year;
        this.popularityCount = 0;
    }

    public int getPopularityCount() {
        return popularityCount;
    }

    public void incrementPopularityCount() {
        popularityCount++;
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
    public void setTitle(String title) {
    this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

class Library {
    private List<Item> items;
    private Map<String, Integer> popularityMap;

    public Library() {
        items = new ArrayList<>();
        popularityMap = new HashMap<>();
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
    
    public int getBookPageCount(String title) {
        for (Item item : items) {
            if (item instanceof Book && item.getTitle().equals(title)) {
                return ((Book) item).getPageCount();
            }
        }
        return -1; 
    }

    public int getBookYear(String title) {
        for (Item item : items) {
            if (item instanceof Book && item.getTitle().equals(title)) {
                return ((Book) item).getYear();
            }
        }
        return -1; 
    }

    public void setBookPageCount(String title, int pageCount) {
        for (Item item : items) {
            if (item instanceof Book && item.getTitle().equals(title)) {
                ((Book) item).setPageCount(pageCount);
            }
        }
    }

    public void setBookYear(String title, int year) {
        for (Item item : items) {
            if (item instanceof Book && item.getTitle().equals(title)) {
                ((Book) item).setYear(year);
            }
        }
    }
    
    
    public void viewBook(String title) {
        if (popularityMap.containsKey(title)) {
            int currentCount = popularityMap.get(title);
            popularityMap.put(title, currentCount + 1);
        } else {
            popularityMap.put(title, 1);
        }

        //incrmnt popularity count
        for (Item item : items) {
            if (item instanceof Book && item.getTitle().equals(title)) {
                ((Book) item).incrementPopularityCount();
                break;
            }
        }
    }

    public int getBookPopularityCount(String title) {
        return popularityMap.getOrDefault(title, 0);
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
                String line;
                if (item instanceof Book) {
                    Book book = (Book) item;
                    line = book.getId() + ", " + book.getTitle() + ", " + book.getAuthor() + ", "
                            + book.getPageCount() + ", " + book.getYear() + ", " + book.getType();
                } else {
                    line = item.getId() + ", " + item.getTitle() + ", " + item.getType();
                }
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

    String[] columnNames = {"ID", "Title", "Author", "Page Count", "Year"};
    tableModel.setColumnIdentifiers(columnNames);
    
    updateTable();
        
        editButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < library.getAvailableItems().size()) {
                Item selectedItem = library.getAvailableItems().get(selectedRow);
                if (selectedItem instanceof Book) {
                    Book selectedBook = (Book) selectedItem;
                    showEditItemDialog(selectedBook);
                } else {
                    JOptionPane.showMessageDialog(frame, "Cannot edit non-book items.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a book to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < library.getAvailableItems().size()) {
                Item selectedItem = library.getAvailableItems().get(selectedRow);
                if (selectedItem instanceof Book) {
                    Book selectedBook = (Book) selectedItem;
                    library.viewBook(selectedBook.getTitle()); //incrmnt popularity count
                    updateTable();
                } else {
                    JOptionPane.showMessageDialog(frame, "Cannot view non-book items.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a book to view.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
                        saveItemsToFile(); 
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
    
    
        private void showEditItemDialog(Book book) {
            JFrame editFrame = new JFrame("Edit Item: " + book.getTitle());
            editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            editFrame.setLayout(new GridLayout(4, 2));

            JLabel titleLabel = new JLabel("Title:");
            JTextField titleField = new JTextField(book.getTitle());
            JLabel authorLabel = new JLabel("Author:");
            JTextField authorField = new JTextField(book.getAuthor());
            JLabel pageCountLabel = new JLabel("Page Count:");
            JTextField pageCountField = new JTextField(String.valueOf(book.getPageCount()));
            JLabel yearLabel = new JLabel("Year:");
            JTextField yearField = new JTextField(String.valueOf(book.getYear()));

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newTitle = titleField.getText();
                    String newAuthor = authorField.getText();
                    int newPageCount = Integer.parseInt(pageCountField.getText());
                    int newYear = Integer.parseInt(yearField.getText());

                    book.setTitle(newTitle);
                    book.setAuthor(newAuthor);
                    book.setPageCount(newPageCount);
                    book.setYear(newYear);

                    JOptionPane.showMessageDialog(editFrame, "Item edited successfully!");
                    updateTable();
                    saveItemsToFile();
                    editFrame.dispose();
                }
            });
            

            editFrame.add(titleLabel);
            editFrame.add(titleField);
            editFrame.add(authorLabel);
            editFrame.add(authorField);
            editFrame.add(pageCountLabel);
            editFrame.add(pageCountField);
            editFrame.add(yearLabel);
            editFrame.add(yearField);
            editFrame.add(saveButton);

            editFrame.pack();
            editFrame.setVisible(true);
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
    
    private void saveItemsToFile() {
        FileHandler fileHandler = new FileHandler();
        fileHandler.saveItemsToFile(library.getItems(), "items.txt");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryManagementSystemGUI();
        });
    }
}
