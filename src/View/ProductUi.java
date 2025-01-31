package View;

import Model.Product;
import Services.ProductServices;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class ProductUI extends JFrame {
    private final ProductServices productService;
    private JTextField txtProductName;
    private JTextField txtProductPrice;
    private JTextField txtProductStock;
    private JTable tblProducts;
    private DefaultTableModel tableModel;

    public ProductUI(ProductServices productService) {
        this.productService = productService;
        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Gestión de Productos");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245)); // Fondo claro

        // Panel Superior - Datos del Producto
        JPanel productPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        productPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Datos del Producto"));
        productPanel.setBackground(new Color(250, 250, 250));

        // Nombre del producto
        productPanel.add(createStyledLabel("Nombre:", Color.DARK_GRAY));
        txtProductName = createStyledTextField();
        productPanel.add(txtProductName);

        // Precio del producto
        productPanel.add(createStyledLabel("Precio:", Color.DARK_GRAY));
        txtProductPrice = createStyledTextField();
        productPanel.add(txtProductPrice);

        // Stock del producto
        productPanel.add(createStyledLabel("Stock:", Color.DARK_GRAY));
        txtProductStock = createStyledTextField();
        productPanel.add(txtProductStock);

        // Botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(new Color(250, 250, 250));

        JButton btnAddProduct = createStyledButton("Agregar Producto", new Color(46, 204, 113), e -> addProduct(e));
        JButton btnEditProduct = createStyledButton("Editar Producto", new Color(52, 152, 219), e -> editProduct(e));
        JButton btnRemoveProduct = createStyledButton("Eliminar Producto", new Color(231, 76, 60), e -> removeProduct(e));

        buttonPanel.add(btnAddProduct);
        buttonPanel.add(btnEditProduct);
        buttonPanel.add(btnRemoveProduct);

        productPanel.add(buttonPanel);
        add(productPanel, BorderLayout.NORTH);

        // Panel de Búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));

        JTextField txtSearchProduct = createStyledTextField();
        txtSearchProduct.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterProducts(txtSearchProduct.getText()); }
            public void removeUpdate(DocumentEvent e) { filterProducts(txtSearchProduct.getText()); }
            public void changedUpdate(DocumentEvent e) { filterProducts(txtSearchProduct.getText()); }
        });

        searchPanel.add(createStyledLabel("Buscar Producto:", Color.DARK_GRAY), BorderLayout.WEST);
        searchPanel.add(txtSearchProduct, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.CENTER);

        // Tabla de Productos
        tableModel = new DefaultTableModel(new Object[]{"Nombre", "Precio", "Stock"}, 0);
        tblProducts = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                c.setForeground(Color.DARK_GRAY);
                return c;
            }
        };
        tblProducts.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblProducts.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tblProducts);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.SOUTH);

        loadProducts();
    }

    // Métodos de estilo (similares a los de CustomerUI)
    private JLabel createStyledLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(color);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setBackground(Color.WHITE);
        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor, ActionListener listener) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 30, 30);

                // Fondo
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 30, 30);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.addActionListener(listener);

        // Animación de hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    private void loadProducts() {
        tableModel.setRowCount(0); // Limpiar la tabla
        List<Product> products = productService.listProducts();
        for (Product product : products) {
            tableModel.addRow(new Object[]{product.getName(), product.getPrice(), product.getStock()});
        }
    }

    private void addProduct(ActionEvent e) {
        String name = txtProductName.getText().trim();
        String priceStr = txtProductPrice.getText().trim();
        String stockStr = txtProductStock.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre, precio y stock.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            productService.addProduct(name, price, stock);
            loadProducts();
            txtProductName.setText("");
            txtProductPrice.setText("");
            txtProductStock.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio y el stock deben ser números válidos.");
        }
    }

    private void editProduct(ActionEvent e) {
        int selectedRow = tblProducts.getSelectedRow();
        if (selectedRow >= 0) {
            Product selectedProduct = productService.listProducts().get(selectedRow);

            // Crear un diálogo de edición personalizado
            JDialog editDialog = new JDialog(this, "Editar Producto", true);
            editDialog.setLayout(new GridLayout(4, 2, 10, 10));
            editDialog.setSize(400, 300);
            editDialog.setLocationRelativeTo(this);

            // Campos de edición
            JLabel lblName = new JLabel("Nombre:");
            JTextField txtName = createStyledTextField();
            txtName.setText(selectedProduct.getName());

            JLabel lblPrice = new JLabel("Precio:");
            JTextField txtPrice = createStyledTextField();
            txtPrice.setText(String.valueOf(selectedProduct.getPrice()));

            JLabel lblStock = new JLabel("Stock:");
            JTextField txtStock = createStyledTextField();
            txtStock.setText(String.valueOf(selectedProduct.getStock()));

            // Botón de guardar
            JButton btnSave = createStyledButton("Guardar Cambios", new Color(46, 204, 113), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Validar campos
                    String name = txtName.getText().trim();
                    String priceStr = txtPrice.getText().trim();
                    String stockStr = txtStock.getText().trim();

                    if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog,
                                "Por favor ingrese un nombre, precio y stock.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        int stock = Integer.parseInt(stockStr);

                        // Actualizar el producto
                        Product updatedProduct = new Product(name, price, stock);
                        productService.editProduct(selectedProduct.getId(), updatedProduct);

                        // Recargar la tabla
                        loadProducts();

                        // Cerrar el diálogo
                        editDialog.dispose();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(editDialog,
                                "El precio y el stock deben ser números válidos.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Botón de cancelar
            JButton btnCancel = createStyledButton("Cancelar", new Color(231, 76, 60), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editDialog.dispose();
                }
            });

            // Agregar componentes al diálogo
            editDialog.add(lblName);
            editDialog.add(txtName);
            editDialog.add(lblPrice);
            editDialog.add(txtPrice);
            editDialog.add(lblStock);
            editDialog.add(txtStock);
            editDialog.add(btnSave);
            editDialog.add(btnCancel);

            // Mostrar el diálogo
            editDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un producto para editar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeProduct(ActionEvent e) {
        int selectedRow = tblProducts.getSelectedRow();
        if (selectedRow >= 0) {
            Product selectedProduct = productService.listProducts().get(selectedRow);
            productService.deleteProduct(selectedProduct.getId());
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un producto para eliminar.");
        }
    }

    private void filterProducts(String query) {
        tableModel.setRowCount(0);
        List<Product> products = productService.listProducts();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                tableModel.addRow(new Object[]{product.getName(), product.getPrice(), product.getStock()});
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductServices productService = new ProductServices();
            ProductUI productUI = new ProductUI(productService);
            productUI.setVisible(true);
        });
    }
}