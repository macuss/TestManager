package view;

import com.formdev.flatlaf.FlatClientProperties;
import model.Escenario;
import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class EscenarioDialog extends JDialog {

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JButton btnGuardar, btnCancelar;
    private boolean guardado = false;

    public EscenarioDialog(Frame parent, String titulo, Escenario escenario) {
        super(parent, titulo, true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        initComponents(escenario);
    }

    private void initComponents(Escenario escenario) {
        setLayout(new BorderLayout());

        JPanel panelFields = new JPanel(new GridBagLayout());
        panelFields.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panelFields.add(new JLabel("Nombre:"), gbc);
        
        txtNombre = new JTextField(escenario != null ? escenario.getNombre() : "");
        txtNombre.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ej: Login de usuario");
        gbc.gridy = 1;
        panelFields.add(txtNombre, gbc);

        // Descripción
        gbc.gridy = 2;
        panelFields.add(new JLabel("Descripción:"), gbc);
        
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setText(escenario != null ? escenario.getDescripcion() : "");
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(txtDescripcion);
        gbc.gridy = 3; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panelFields.add(spDesc, gbc);

        // Botones
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnGuardar.putClientProperty(FlatClientProperties.STYLE, "background: $Component.accentColor; foreground: #ffffff");
        
        btnCancelar = new JButton("Cancelar");

        panelButtons.add(btnCancelar);
        panelButtons.add(btnGuardar);

        add(panelFields, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        // Listeners
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
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