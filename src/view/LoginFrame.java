package view;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import controller.LoginController;
import java.awt.*;

@SuppressWarnings("serial")
public class LoginFrame extends JFrame {

    private JTextField txtAlias;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("TestManager - Acceso");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 550); 
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel Principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIManager.getColor("Panel.background"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30); // Margen interno
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TÍTULO
        JLabel lblTitle = new JLabel("Bienvenido", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(lblTitle, gbc);

        // ALIAS
        JLabel label = new JLabel("Alias");
        label.putClientProperty(FlatClientProperties.STYLE, "font: $h4.font");
        gbc.gridy = 1; gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(label, gbc);

        txtAlias = new JTextField();
        // Propiedades de FlatLaf para placeholder y estilo
        txtAlias.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingresa tu usuario");
        txtAlias.putClientProperty(FlatClientProperties.STYLE, "arc: 10"); 
        txtAlias.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 2;
        mainPanel.add(txtAlias, gbc);

        // PASSWORD
        JLabel label_1 = new JLabel("Contraseña");
        label_1.putClientProperty(FlatClientProperties.STYLE, "font: $h4.font");
        gbc.gridy = 3; gbc.insets = new Insets(15, 0, 5, 0);
        mainPanel.add(label_1, gbc);

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••••");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "arc: 10; showRevealButton: true");
        txtPassword.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        mainPanel.add(txtPassword, gbc);

        //BOTÓN LOGIN 
        JButton btnLogin = new JButton("Iniciar Sesión");
        // Estilo de botón "Acentuado"
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: $Component.accentColor; foreground: #ffffff");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 45));
        gbc.gridy = 5;
        gbc.insets = new Insets(40, 0, 10, 0);
        mainPanel.add(btnLogin, gbc);

        //BOTÓN REGISTRO
        JButton btnRegistro = new JButton("¿No tienes cuenta? Regístrate");
        btnRegistro.setBorderPainted(false);
        btnRegistro.setContentAreaFilled(false);
        btnRegistro.setForeground(UIManager.getColor("Component.focusColor"));
        btnRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(btnRegistro, gbc);

        getContentPane().add(mainPanel);

        // Controller
        LoginController controller = new LoginController(this);
        btnLogin.addActionListener(e -> controller.login());
        btnRegistro.addActionListener(e -> controller.irARegistro());
        
        // Enter para loguear
        getRootPane().setDefaultButton(btnLogin);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                txtAlias.requestFocusInWindow();
            }
        });
        
    }

    public String getAlias() { return txtAlias.getText(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
}