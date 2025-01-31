package View;

import Services.BillServices;
import Services.ProductServices;
import Services.CustomerService;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;


class MainUI extends JFrame {
    // Variables para mover la ventana
    private Point mouseDownCompCoords = null;
    private boolean isMaximized = false;
    private Rectangle normalBounds;

    public MainUI() {
        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Sistema de Gestión Empresarial");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setUndecorated(true); // Eliminar bordes predeterminados
        setBackground(new Color(0, 0, 0, 0)); // Fondo transparente

        // Panel principal con fondo degradado
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint gradient = new GradientPaint(0, 0, new Color(93, 173, 226),
                        getWidth(), getHeight(), new Color(46, 134, 222));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de control de ventana
        JPanel windowControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        windowControlPanel.setOpaque(false);

        // Botón de minimizar
        JButton minimizeButton = createWindowControlButton("─", new Color(241, 196, 15));
        minimizeButton.addActionListener(e -> setState(Frame.ICONIFIED));

        // Botón de maximizar/restaurar
        JButton maximizeButton = createWindowControlButton("□", new Color(46, 204, 113));
        maximizeButton.addActionListener(e -> toggleMaximize(maximizeButton));

        // Botón de cerrar
        JButton closeButton = createWindowControlButton("✕", new Color(231, 76, 60));
        closeButton.addActionListener(e -> System.exit(0));

        windowControlPanel.add(minimizeButton);
        windowControlPanel.add(maximizeButton);
        windowControlPanel.add(closeButton);

        mainPanel.add(windowControlPanel, BorderLayout.NORTH);

        // Hacer la ventana movible
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });

        // Título superior
        JLabel titleLabel = new JLabel("Perfumex", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel central con imagen y botones
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setOpaque(false);

        // Cargar imagen
        String imagePath = "/Images/fotoPerfumex.jpg"; // Ruta relativa a src
        URL imageLocation = getClass().getResource(imagePath);

        if (imageLocation != null) {
            JLabel logoLabel = new JLabel(new ImageIcon(imageLocation));
            centerPanel.add(logoLabel, BorderLayout.CENTER);
        } else {
            System.err.println("Error: No se pudo cargar la imagen desde la ruta: " + imagePath);
            JLabel placeholder = new JLabel("Logo no disponible", SwingConstants.CENTER);
            placeholder.setFont(new Font("Arial", Font.BOLD, 24));
            placeholder.setForeground(Color.WHITE);
            centerPanel.add(placeholder, BorderLayout.CENTER);
        }

        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        buttonPanel.setOpaque(false);

        JButton customerButton = new JButton("Clientes");
        customerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        customerButton.setForeground(Color.WHITE);
        customerButton.setBackground(new Color(231, 76, 60));
        customerButton.addActionListener(e -> openCustomerUI());

        JButton productButton = new JButton("Productos");
        productButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        productButton.setForeground(Color.WHITE);
        productButton.setBackground(new Color(46, 204, 113));
        productButton.addActionListener(e -> openProductUI());

        JButton billButton = new JButton("Facturas");
        billButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        billButton.setForeground(Color.WHITE);
        billButton.setBackground(new Color(52, 152, 219));
        billButton.addActionListener(e -> openBillUI());

        buttonPanel.add(customerButton);
        buttonPanel.add(productButton);
        buttonPanel.add(billButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Pie de página
        JLabel footerLabel = new JLabel("© 2023 Sistema de Gestión Empresarial", SwingConstants.CENTER);
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Método para crear botones de control de ventana
    private JButton createWindowControlButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    // Método para alternar entre maximizar y restaurar
    private void toggleMaximize(JButton maximizeButton) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();

        if (!isMaximized) {
            // Guardar el tamaño y posición actual
            normalBounds = getBounds();

            // Maximizar
            defaultScreen.setFullScreenWindow(this);
            setBounds(defaultScreen.getDefaultConfiguration().getBounds());
            maximizeButton.setText("❐"); // Cambiar ícono a restaurar
            isMaximized = true;
        } else {
            // Restaurar
            defaultScreen.setFullScreenWindow(null);
            setBounds(normalBounds);
            maximizeButton.setText("□"); // Cambiar ícono a maximizar
            isMaximized = false;
        }
    }

    // Métodos para abrir interfaces
    private void openCustomerUI() {
        CustomerService customerService = new CustomerService();
        CustomerUI customerUI = new CustomerUI(customerService);
        customerUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customerUI.setVisible(true);
    }

    private void openProductUI() {
        ProductServices productService = new ProductServices();
        ProductUI productUI = new ProductUI(productService);
        productUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        productUI.setVisible(true);
    }

    private void openBillUI() {
        BillUI billUI = new BillUI();
        billUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        billUI.setVisible(true);
    }

    // Método para crear botones con estilo y animaciones
    private JButton createStyledButton(String text, String iconPath, Color bgColor) {
        JButton btn = new JButton(text, new ImageIcon(getClass().getResource(iconPath))) {
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

        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        // Animación de hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    // Método principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Establecer Look and Feel multiplataforma
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Crear y mostrar la ventana principal
            MainUI mainUI = new MainUI();
            mainUI.setVisible(true);
        });
    }
}