package view;

import com.formdev.flatlaf.FlatClientProperties;
import dao.UsuarioDAO;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class RegistroFrame extends JFrame {

    private JTextField txtAlias, txtNombre, txtApellido, txtLegajo;
    private JPasswordField txtPassword;
    private JButton btnRegistrar, btnVolver;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public RegistroFrame() {
        setTitle("Registro de Usuario - QA System");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initAndLayout();
        setupListeners();
    }

    private void initAndLayout() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Título
        JLabel lblTitulo = new JLabel("Crear Cuenta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitulo, gbc);

        gbc.gridwidth = 1; // Reset width

        // Campos
        txtAlias = new JTextField();
        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtLegajo = new JTextField();
        txtPassword = new JPasswordField();

        // Placeholders (FlatLaf)
        txtAlias.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Alias / Usuario");
        txtNombre.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tu nombre");
        txtApellido.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tu apellido");
        txtLegajo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nro de Legajo");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Contraseña");

        
        gbc.gridy = 1; mainPanel.add(new JLabel("Alias:"), gbc);
        gbc.gridy = 2; mainPanel.add(txtAlias, gbc);
        
        gbc.gridy = 3; mainPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridy = 4; mainPanel.add(txtNombre, gbc);
        
        gbc.gridy = 5; mainPanel.add(new JLabel("Apellido:"), gbc);
        gbc.gridy = 6; mainPanel.add(txtApellido, gbc);
        
        gbc.gridy = 7; mainPanel.add(new JLabel("Legajo:"), gbc);
        gbc.gridy = 8; mainPanel.add(txtLegajo, gbc);
        
        gbc.gridy = 9; mainPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridy = 10; mainPanel.add(txtPassword, gbc);

        // Botones
        btnRegistrar = new JButton("Registrarme");
        btnRegistrar.putClientProperty(FlatClientProperties.STYLE, "background: $Component.accentColor; foreground: #ffffff; font: bold");
        
        btnVolver = new JButton("Volver al Login");

        gbc.gridy = 11; gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(btnRegistrar, gbc);
        
        gbc.gridy = 12; gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(btnVolver, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {
        btnRegistrar.addActionListener(e -> ejecutarRegistro());

        btnVolver.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        // Enter para registrar
        getRootPane().setDefaultButton(btnRegistrar);
    }

    private void ejecutarRegistro() {
        String alias = txtAlias.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String legajo = txtLegajo.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (alias.isEmpty() || nombre.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete los campos obligatorios (Alias, Nombre y Pass)");
            return;
        }

        Usuario nuevo = new Usuario(0, alias, nombre, apellido, legajo, pass, "tester");
        usuarioDAO.registrar(nuevo);

        JOptionPane.showMessageDialog(this, "¡Usuario registrado con éxito! Ya podés iniciar sesión.");
        
        new LoginFrame().setVisible(true);
        dispose();
    }
}