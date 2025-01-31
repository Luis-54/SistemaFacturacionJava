package View;

import Model.Bill;
import Model.Customer;
import Model.Product;
import Services.BillServices;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

class BillUI extends JFrame {
    private final BillServices billService;
    private String currentDraftId;
    private Customer currentCustomer;

    // Componentes UI
    private JTextField txtCustomerName;
    private JCheckBox chkFrequentCustomer;
    private JTextField txtDiscount;
    private JTextField txtSearchProduct;
    private JTable tblProducts;
    private DefaultTableModel tableModel;
    private JLabel lblSubtotal;
    private JLabel lblDiscount;
    private JLabel lblTax;
    private JLabel lblTotal;
    private final DecimalFormat formatter = new DecimalFormat("#,##0.00");

    public BillUI() {
        billService = new BillServices();
        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Sistema de Facturación");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245)); // Fondo claro

        // Panel Superior - Datos del Cliente
        JPanel customerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        customerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Datos del Cliente"));
        customerPanel.setBackground(new Color(250, 250, 250));

        // Nombre del cliente
        customerPanel.add(createStyledLabel("Nombre:", Color.DARK_GRAY));
        txtCustomerName = createStyledTextField();
        customerPanel.add(txtCustomerName);

        // Cliente frecuente
        customerPanel.add(createStyledLabel("Cliente Frecuente:", Color.DARK_GRAY));
        chkFrequentCustomer = new JCheckBox("Cliente Frecuente");
        chkFrequentCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkFrequentCustomer.setForeground(Color.DARK_GRAY);
        chkFrequentCustomer.setBackground(new Color(250, 250, 250));
        customerPanel.add(chkFrequentCustomer);

        // Descuento
        customerPanel.add(createStyledLabel("Descuento (%):", Color.DARK_GRAY));
        txtDiscount = createStyledTextField();
        txtDiscount.setText("0.0");
        customerPanel.add(txtDiscount);

        // Botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(new Color(250, 250, 250));

        JButton btnStartDraft = createStyledButton("Iniciar Factura", new Color(46, 204, 113), e -> startNewDraft(e));
        JButton btnSelectCustomer = createStyledButton("Seleccionar Cliente", new Color(52, 152, 219), e -> selectExistingCustomer());
        JButton btnSearchProduct = createStyledButton("Buscar Producto", new Color(241, 196, 15), e -> showProductSelectionDialog());

        buttonPanel.add(btnStartDraft);
        buttonPanel.add(btnSelectCustomer);
        buttonPanel.add(btnSearchProduct);

        customerPanel.add(buttonPanel);
        add(customerPanel, BorderLayout.NORTH);

        // Panel de Búsqueda de Productos
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));

        txtSearchProduct = createStyledTextField();
        txtSearchProduct.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterProducts(); }
            public void removeUpdate(DocumentEvent e) { filterProducts(); }
            public void changedUpdate(DocumentEvent e) { filterProducts(); }
        });

        searchPanel.add(createStyledLabel("Buscar Producto:", Color.DARK_GRAY), BorderLayout.WEST);
        searchPanel.add(txtSearchProduct, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.CENTER);

        // Tabla de Productos
        tableModel = new DefaultTableModel(new Object[]{"Código", "Nombre", "Precio", "Precio + IVA"}, 0);
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

        // Panel de Totales
        JPanel totalsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        totalsPanel.setBorder(BorderFactory.createTitledBorder("Totales"));
        totalsPanel.setBackground(new Color(250, 250, 250));

        String[] totalLabels = {"Subtotal:", "Descuento:", "IVA:", "Total:"};
        JLabel[] totalValueLabels = {lblSubtotal, lblDiscount, lblTax, lblTotal};

        for (int i = 0; i < totalLabels.length; i++) {
            JLabel label = createStyledLabel(totalLabels[i], Color.DARK_GRAY);
            totalValueLabels[i] = createStyledLabel("0.00", Color.DARK_GRAY);
            totalsPanel.add(label);
            totalsPanel.add(totalValueLabels[i]);
        }

        lblSubtotal = totalValueLabels[0];
        lblDiscount = totalValueLabels[1];
        lblTax = totalValueLabels[2];
        lblTotal = totalValueLabels[3];

        add(totalsPanel, BorderLayout.EAST);
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

    private JButton createStyledButton(String text, Color bgColor, ActionListener listener ) {
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

    private void startNewDraft(ActionEvent event) {
        currentDraftId = billService.startNewDraft(currentCustomer);
        updateTotals();
    }

    private void selectExistingCustomer() {
        // Lógica para seleccionar un cliente existente
    }

    private void showProductSelectionDialog() {
        // Lógica para mostrar un diálogo de selección de productos
    }

    private void filterProducts() {
        String searchTerm = txtSearchProduct.getText().toLowerCase();
        tableModel.setRowCount(0); // Limpiar la tabla

        for (Product product : billService.getAllProducts()) {
            if (product.getName().toLowerCase().contains(searchTerm)) {
                tableModel.addRow(new Object[]{product.getId(), product.getName(), product.getPrice(), billService.getProductPriceWithTax(product)});
            }
        }
    }

    private void updateTotals() {
        Map<String, Double> details = billService.getDraftPriceDetails(currentDraftId);
        lblSubtotal.setText(formatter.format(details.get("subtotal")));
        lblDiscount.setText(formatter.format(details.get("discount")));
        lblTax.setText(formatter.format(details.get("tax")));
        lblTotal.setText(formatter.format(details.get("total")));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BillUI ui = new BillUI();
            ui.setVisible(true);
        });
    }
}