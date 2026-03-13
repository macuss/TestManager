package view;

import com.formdev.flatlaf.FlatClientProperties;
import model.Proyecto;
import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class ProyectoDialog extends JDialog {

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JButton btnGuardar, btnCancelar;
    private boolean guardado = false;

    public ProyectoDialog(Frame parent, String titulo, Proyecto proyecto) {
        super(parent, titulo, true);
        setSize(400, 320);
        setLocationRelativeTo(parent);
        initComponents(proyecto);
    }

    private void initComponents(Proyecto proyecto) {
        setLayout(new BorderLayout());

        JPanel panelFields = new JPanel(new GridBagLayout());
        panelFields.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Nombre
        gbc.gridy = 0;
        panelFields.add(new JLabel("Nombre del Proyecto:"), gbc);
        
        txtNombre = new JTextField(proyecto != null ? proyecto.getNombre() : "");
        txtNombre.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ej: App Móvil v1.0");
        gbc.gridy = 1;
        panelFields.add(txtNombre, gbc);

        // Descripción
        gbc.gridy = 2;
        panelFields.add(new JLabel("Descripción / Objetivo:"), gbc);
        
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setText(proyecto != null ? proyecto.getDescripcion() : "");
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(txtDescripcion);
        gbc.gridy = 3; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panelFields.add(spDesc, gbc);

        // Botones
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        btnGuardar = new JButton("Guardar Proyecto");
        btnGuardar.putClientProperty(FlatClientProperties.STYLE, "background: $Component.accentColor; foreground: #ffffff; font: bold");
        
        btnCancelar = new JButton("Cancelar");

        panelButtons.add(btnCancelar);
        panelButtons.add(btnGuardar);

        add(panelFields, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        // Listeners
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del proyecto es obligatorio", "Validación", JOptionPane.WARNING_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            guardado = true;
            dispose();
        });

        getRootPane().setDefaultButton(btnGuardar);
        

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0);
        String dispatchWindowClosingActionName = "dispatchWindowClosingAction";

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, dispatchWindowClosingActionName);
        getRootPane().getActionMap().put(dispatchWindowClosingActionName, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        
        this.getRootPane().setDefaultButton(btnGuardar);
    }

    public boolean isGuardado() { return guardado; }
    public String getNombre() { return txtNombre.getText().trim(); }
    public String getDescripcion() { return txtDescripcion.getText().trim(); }
}