package view;

import com.formdev.flatlaf.FlatClientProperties;
import model.CasoPrueba;
import model.Estado;
import javax.swing.*;
import java.awt.*;

@SuppressWarnings({ "serial", "unused" })
public class CasoPruebaDialog extends JDialog {
    private JTextField txtNombre = new JTextField();
    private JTextField txtCriterio = new JTextField();
    
    // Uso JTextArea para los pasos Gherkin
    private JTextArea txtGiven = new JTextArea(3, 20);
    private JTextArea txtWhen = new JTextArea(3, 20);
    private JTextArea txtThen = new JTextArea(3, 20);
    
    private boolean aprobado = false;

    public CasoPruebaDialog(Frame parent, String titulo) {
        super(parent, titulo, true); // Modal
        setSize(550, 650);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
   
        JPanel container = new JPanel(new GridBagLayout());
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

     
        txtGiven.setLineWrap(true);
        txtWhen.setLineWrap(true);
        txtThen.setLineWrap(true);

     
        addHeader(container, "Información Básica", 0);
        
        gbc.gridy = 1; container.add(new JLabel("Nombre del Caso:"), gbc);
        gbc.gridy = 2; container.add(txtNombre, gbc);
        
        gbc.gridy = 3; container.add(new JLabel("Criterio de Aceptación:"), gbc);
        gbc.gridy = 4; container.add(txtCriterio, gbc);

        addHeader(container, "Pasos Gherkin", 5);

        gbc.gridy = 6; container.add(new JLabel("GIVEN:"), gbc);
        gbc.gridy = 7; container.add(new JScrollPane(txtGiven), gbc);

        gbc.gridy = 8; container.add(new JLabel("WHEN:"), gbc);
        gbc.gridy = 9; container.add(new JScrollPane(txtWhen), gbc);

        gbc.gridy = 10; container.add(new JLabel("THEN:"), gbc);
        gbc.gridy = 11; container.add(new JScrollPane(txtThen), gbc);

        // Botones
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Confirmar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> { aprobado = true; dispose(); });
        btnCancelar.addActionListener(e -> dispose());
        
        panelButtons.add(btnCancelar);
        panelButtons.add(btnGuardar);

        setLayout(new BorderLayout());
        add(new JScrollPane(container), BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
    }

    private void addHeader(JPanel p, String text, int y) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(UIManager.getColor("Component.accentColor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = y; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        p.add(label, gbc);
    }

    // Este método es el que llama ProyectoPanel para obtener el objeto ya armado
    public CasoPrueba getCaso(int escenarioId) {
        if (!aprobado) return null;
        
        return new CasoPrueba(
            txtNombre.getText(),
            "", 
            Estado.LISTO,
            escenarioId,
            txtCriterio.getText(),
            txtGiven.getText(),
            txtWhen.getText(),
            txtThen.getText()
        );
    }
}