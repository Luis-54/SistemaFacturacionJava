package View;

import Model.Product;
import Services.ProductServices;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

class ProductUI extends JFrame {
    private ProductServices productServices;
    private JTextField idField, nameField, priceField, stockField;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, clearButton;

    public ProductUI() {
        productServices = new ProductServices();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        refreshProductTable();
    }

    private void initializeComponents() {
        // Modern, clean font
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Set up input fields with modern styling
        idField = createStyledTextField();
        nameField = createStyledTextField();
        priceField = createStyledTextField();
        stockField = createStyledTextField();

        // Create table with modern look
        String[] columnNames = {"ID", "Name", "Price", "Stock"};
        tableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(tableModel);
        productTable.setFont(defaultFont);
        productTable.setRowHeight(25);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Styled buttons
        addButton = createStyledButton("Add Product", Color.GREEN.darker());
        editButton = createStyledButton("Edit Product", Color.ORANGE.darker());
        deleteButton = createStyledButton("Delete Product", Color.RED.darker());
        clearButton = createStyledButton("Clear", Color.BLUE.darker());
    }

    private void setupLayout() {
        setTitle("Modern Product Management");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Stock:"));
        inputPanel.add(stockField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Added vertical padding
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Table Panel
        JScrollPane tableScrollPane = new JScrollPane(productTable);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> addProduct());
        editButton.addActionListener(e -> editProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearFields());

        // Add table row selection listener
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                int selectedRow = productTable.getSelectedRow();
                idField.setText(productTable.getValueAt(selectedRow, 0).toString());
                nameField.setText(productTable.getValueAt(selectedRow, 1).toString());
                priceField.setText(productTable.getValueAt(selectedRow, 2).toString());
                stockField.setText(productTable.getValueAt(selectedRow, 3).toString());
            }
        });
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return button;
    }

    private void addProduct() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            Product product = new Product(id, name, price, stock);
            productServices.addProduct(product);
            refreshProductTable();
            clearFields();
            showMessage("Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            showMessage("Invalid input! Please check your entries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProduct() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            Product newProduct = new Product(id, name, price, stock);
            boolean success = productServices.editarProduct(id, newProduct);

            if (success) {
                refreshProductTable();
                clearFields();
                showMessage("Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage("Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            showMessage("Invalid input! Please check your entries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        try {
            int id = Integer.parseInt(idField.getText());
            boolean success = productServices.deleteProduct(id);

            if (success) {
                refreshProductTable();
                clearFields();
                showMessage("Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage("Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            showMessage("Invalid input! Please check your entries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshProductTable() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch and populate products
        List<Product> products = productServices.listaProducts();
        for (Product product : products) {
            tableModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    String.format("$%.2f", product.getPrice()),
                    product.getStock()
            });
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        productTable.clearSelection();
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Cross-platform look and feel that works consistently
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // Fallback to default if setting look and feel fails
                e.printStackTrace();
            }

            ProductUI ui = new ProductUI();
            ui.setLocationRelativeTo(null); // Center on screen
            ui.setVisible(true);
        });
    }
}