package View;

import Model.Customer;
import Services.BillServices;
import Services.CustomerService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

 class CustomerUI extends JFrame {

     private final CustomerService customerService;
    private JTextField txtCustomerName;
    private JTextField txtCustomerId; // Campo para el número de cédula
    private JCheckBox chkFrequentCustomer;
    private JTable tblCustomers;
    private DefaultTableModel tableModel;

    public CustomerUI(CustomerService customerService) {
        this.customerService = customerService;
        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Gestión de Clientes");
        setSize(1200, 710);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245)); // Fondo claro

        // Panel Superior - Datos del Cliente
        JPanel customerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        customerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Datos del Cliente"));
        customerPanel.setBackground(new Color(250, 250, 250));

        // Nombre del cliente
        customerPanel.add(createStyledLabel("Nombre:", Color.DARK_GRAY));
        txtCustomerName = createStyledTextField();
        customerPanel.add(txtCustomerName);

        // Cédula del cliente
        customerPanel.add(createStyledLabel("Cédula:", Color.DARK_GRAY));
        txtCustomerId = createStyledTextField();
        customerPanel.add(txtCustomerId);

        // Cliente frecuente
        chkFrequentCustomer = new JCheckBox("Cliente Frecuente");
        chkFrequentCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkFrequentCustomer.setForeground(Color.DARK_GRAY);
        chkFrequentCustomer.setBackground(new Color(250, 250, 250));
        customerPanel.add(chkFrequentCustomer);

        // Botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(new Color(250, 250, 250));

        JButton btnAddCustomer = createStyledButton("Agregar Cliente", new Color(46, 204, 113), e -> addCustomer(e));
        JButton btnEditCustomer = createStyledButton("Editar Cliente", new Color(52, 152, 219), e -> editCustomer(e));
        JButton btnRemoveCustomer = createStyledButton("Eliminar Cliente", new Color(231, 76, 60), e -> removeCustomer(e));

        buttonPanel.add(btnAddCustomer);
        buttonPanel.add(btnEditCustomer);
        buttonPanel.add(btnRemoveCustomer);

        customerPanel.add(buttonPanel);
        add(customerPanel, BorderLayout.NORTH);

        // Panel de Búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));

        JTextField txtSearchCustomer = createStyledTextField();
        txtSearchCustomer.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterCustomers(txtSearchCustomer.getText()); }
            public void removeUpdate(DocumentEvent e) { filterCustomers(txtSearchCustomer.getText()); }
            public void changedUpdate(DocumentEvent e) { filterCustomers(txtSearchCustomer.getText()); }
        });

        searchPanel.add(createStyledLabel("Buscar Cliente (Nombre o Cédula):", Color.DARK_GRAY), BorderLayout.WEST);
        searchPanel.add(txtSearchCustomer, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.CENTER);

        // Tabla de Clientes
        tableModel = new DefaultTableModel(new Object[]{"Nombre", "Cédula", "Frecuente"}, 0);
        tblCustomers = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                c.setForeground(Color.DARK_GRAY);
                return c;
            }
        };
        tblCustomers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblCustomers.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tblCustomers);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.SOUTH);

        loadCustomers();
    }

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

    private void loadCustomers() {
        tableModel.setRowCount(0); // Limpiar la tabla
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{customer.getName(), customer.getCedula(), customer.isCustomerFrequent()});
        }
    }

    private void addCustomer(ActionEvent e) {
        String name = txtCustomerName.getText().trim();
        String cedulaStr = txtCustomerId.getText().trim();
        boolean isFrequent = chkFrequentCustomer.isSelected();

        if (name.isEmpty() || cedulaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre y una cédula.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int cedula = Integer.parseInt(cedulaStr);

            // Verificar si el cliente ya existe (por nombre o cédula)
            if (customerService.getAllCustomers().stream().anyMatch(c ->
                    c.getName().equalsIgnoreCase(name) || c.getCedula() == cedula)) {
                JOptionPane.showMessageDialog(this, "El cliente ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Agregar el nuevo cliente
            Customer newCustomer = new Customer(name, cedula, isFrequent, isFrequent ? 10.0 : 0.0);
            customerService.getAllCustomers().add(newCustomer);
            loadCustomers(); // Recargar la tabla
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cédula debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterCustomers(String query) {
        tableModel.setRowCount(0); // Limpiar la tabla
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer customer : customers) {
            if (customer.getName().toLowerCase().contains(query.toLowerCase()) ||
                    String.valueOf(customer.getCedula()).contains(query)) {
                tableModel.addRow(new Object[]{customer.getName(), customer.getCedula(), customer.isCustomerFrequent()});
            }
        }
    }

     private void editCustomer(ActionEvent e) {
         int selectedRow = tblCustomers.getSelectedRow();
         if (selectedRow >= 0) {
             Customer selectedCustomer = customerService.getAllCustomers().get(selectedRow);

             // Crear un diálogo de edición personalizado
             JDialog editDialog = new JDialog(this, "Editar Cliente", true);
             editDialog.setLayout(new GridLayout(4, 2, 10, 10));
             editDialog.setSize(400, 300);
             editDialog.setLocationRelativeTo(this);

             // Campos de edición
             JLabel lblName = new JLabel("Nombre:");
             JTextField txtName = createStyledTextField();
             txtName.setText(selectedCustomer.getName());

             JLabel lblCedula = new JLabel("Cédula:");
             JTextField txtCedula = createStyledTextField();
             txtCedula.setText(String.valueOf(selectedCustomer.getCedula()));

             JLabel lblFrequent = new JLabel("Cliente Frecuente:");
             JCheckBox chkFrequent = new JCheckBox();
             chkFrequent.setSelected(selectedCustomer.isCustomerFrequent());

             // Botón de guardar
             JButton btnSave = createStyledButton("Guardar Cambios", new Color(46, 204, 113), new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent e) {
                     // Validar campos
                     String name = txtName.getText().trim();
                     String cedulaStr = txtCedula.getText().trim();

                     if (name.isEmpty() || cedulaStr.isEmpty()) {
                         JOptionPane.showMessageDialog(editDialog,
                                 "Por favor ingrese un nombre y una cédula.",
                                 "Error",
                                 JOptionPane.ERROR_MESSAGE);
                         return;
                     }

                     try {
                         int cedula = Integer.parseInt(cedulaStr);

                         // Actualizar el cliente
                         selectedCustomer.setName(name);
                         selectedCustomer.setCedula(cedula);
                         selectedCustomer.setCustomerFrequent(chkFrequent.isSelected());

                         // Recargar la tabla
                         loadCustomers();

                         // Cerrar el diálogo
                         editDialog.dispose();
                     } catch (NumberFormatException ex) {
                         JOptionPane.showMessageDialog(editDialog,
                                 "La cédula debe ser un número válido.",
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
             editDialog.add(lblCedula);
             editDialog.add(txtCedula);
             editDialog.add(lblFrequent);
             editDialog.add(chkFrequent);
             editDialog.add(btnSave);
             editDialog.add(btnCancel);

             // Mostrar el diálogo
             editDialog.setVisible(true);
         } else {
             JOptionPane.showMessageDialog(this,
                     "Por favor seleccione un cliente para editar.",
                     "Error",
                     JOptionPane.ERROR_MESSAGE);
         }
     }

    private void removeCustomer(ActionEvent e) {
        int selectedRow = tblCustomers.getSelectedRow();
        if (selectedRow >= 0) {
            customerService.getAllCustomers().remove(selectedRow);
            loadCustomers();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un cliente para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtCustomerName.setText("");
        txtCustomerId.setText("");
        chkFrequentCustomer.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomerService customerService = new CustomerService(); // Asegúrate de inicializar correctamente el servicio
            CustomerUI customerUI = new CustomerUI(customerService);
            customerUI.setVisible(true);
        });
    }
}