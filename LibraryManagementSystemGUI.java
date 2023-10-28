package test;

import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

class Item {
    private int id;
    private String title;
    private int popularity;

    public Item(int id, String title, int popularity) {
        this.id = id;
        this.title = title;
        this.popularity = popularity;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPopularity() {
        return popularity;
    }
}


class Book extends Item {
    private String author;
    private int pageCount;
    private int year;

    public Book(int id,String title, String author, int popularity, int pageCount, int year) {
        super(id, title, popularity);
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
                    int popularity = Integer.parseInt(parts[2].trim());
                    Item item = new Item(id, title, popularity);
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
                String line = item.getId() + ", " + item.getTitle() + ", " + item.getPopularity();
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

        // Initialize GUI components
        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create table
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create buttons
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

        // Add event handlers for buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddItemDialog();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement edit item functionality
                // ...
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

        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField();
        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();
        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            
                    @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idField.getText());
                String title = titleField.getText();
                String author = authorField.getText();
                if (!title.isEmpty() && !author.isEmpty()) {
                    boolean itemAdded = library.addItem(new Book(id, title, author, 0, 0, 0));
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
        // Implement a new screen to display the popularity chart (bar chart) using Graphics
        JFrame popularityFrame = new JFrame("Popularity Chart");
        popularityFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popularityFrame.setLayout(new BorderLayout());

        // JPanel to draw the chart
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
               // Get the popularity data from the items list
    
               List<Item> items = library.getItems();
        int[] popularityData = new int[items.size()];
        for (int i = 0; i < items.size(); i++) {
            popularityData[i] = items.get(i).getPopularity();
        }

        // Determine the maximum popularity value
        int maxPopularity = 0;
        for (int popularity : popularityData) {
            if (popularity > maxPopularity) {
                maxPopularity = popularity;
            }
        }

        // Define the dimensions of the chart
        int chartWidth = getWidth() - 40;
        int chartHeight = getHeight() - 40;
        int barWidth = chartWidth / popularityData.length;

        // Draw the bars of the chart
        for (int i = 0; i < popularityData.length; i++) {
            int barHeight = (int) ((double) popularityData[i] / maxPopularity * chartHeight);
            int x = 20 + i * barWidth;
            int y = getHeight() - 20 - barHeight;
            g.setColor(Color.BLUE);
            g.fillRect(x, y, barWidth, barHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, barWidth, barHeight);
        }
                }
            };
            popularityFrame.add(chartPanel, BorderLayout.CENTER);

            // Set up frame properties for the popularity chart screen
            popularityFrame.setSize(400, 300);
            popularityFrame.setLocationRelativeTo(null); // Center the frame on the screen
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
