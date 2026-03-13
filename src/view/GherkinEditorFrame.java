package view;

import com.formdev.flatlaf.FlatClientProperties;
import dao.CasoPruebaDAO;
import dao.AdjuntoDAO;
import model.Adjunto;
import model.CasoPrueba;
import model.Estado;
import java.util.List;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.awt.Desktop;



@SuppressWarnings("serial")
public class GherkinEditorFrame extends JFrame {

    private JTextField txtNombre;
    private JTextArea txtCriterio;
    private JTextArea txtGiven, txtWhen, txtThen;
    private JTextPane txtPreview;
    private JComboBox<Estado> comboEstado;
    private ProyectoPanel panelPadre;
    

    private DefaultListModel<String> modelAdjuntos;
    private JList<String> listAdjuntos;
    private AdjuntoDAO adjuntoDAO = new AdjuntoDAO();

    private CasoPrueba caso;
    private CasoPruebaDAO casoDAO = new CasoPruebaDAO();

    public GherkinEditorFrame(CasoPrueba caso, ProyectoPanel panelPadre) {
        this.caso = caso;
        this.panelPadre = panelPadre;

        setTitle("Editor Gherkin - " + caso.getNombre());
        setSize(1200, 800); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        layoutComponents(); // configura la UI y los listeners
        agregarAutoPreview();
        generarPreview();
        
        if (this.caso != null && this.caso.getId() > 0) {
            cargarListaAdjuntos();
        }

         // para guardar con Ctrl+S
        getRootPane().registerKeyboardAction(
                e -> guardarCambios(),
                KeyStroke.getKeyStroke("control S"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // para cerrar con la tecla ESC
        getRootPane().registerKeyboardAction(
                e -> guardarCambios(), 
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
    }

    private void initComponents() {
        txtNombre = new JTextField(caso.getNombre());
        txtCriterio = new JTextArea(caso.getCriterioAceptacion());
        txtGiven = new JTextArea(caso.getGivenStep());
        txtWhen = new JTextArea(caso.getWhenStep());
        txtThen = new JTextArea(caso.getThenStep());
        
        txtNombre.putClientProperty(FlatClientProperties.STYLE, "font: $h3.font; arc: 8");
        estilizarArea(txtGiven, "Dado que...");
        estilizarArea(txtWhen, "Cuando ocurre...");
        estilizarArea(txtThen, "Entonces se espera...");
        estilizarArea(txtCriterio, "Describa el criterio de aceptación aquí...");

        comboEstado = new JComboBox<>(Estado.values());
        comboEstado.setSelectedItem(caso.getEstado());

        txtPreview = new JTextPane();
        txtPreview.setEditable(false);
        txtPreview.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtPreview.setBackground(UIManager.getColor("EditorPane.background"));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // PANEL DE EDICIÓN (IZQUIERDA)
        JPanel panelEdicion = new JPanel(new GridBagLayout());
        panelEdicion.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0; addHeader(panelEdicion, "Nombre del Escenario", gbc);
        gbc.gridy = 1; panelEdicion.add(txtNombre, gbc);
        
        gbc.weighty = 0.1;
        gbc.gridy = 2; addHeader(panelEdicion, "Criterio de Aceptación", gbc);
        gbc.gridy = 3; panelEdicion.add(new JScrollPane(txtCriterio), gbc);

        gbc.weighty = 0.3;
        gbc.gridy = 4; addHeader(panelEdicion, "GIVEN (Precondiciones)", gbc);
        gbc.gridy = 5; panelEdicion.add(new JScrollPane(txtGiven), gbc);

        gbc.gridy = 6; addHeader(panelEdicion, "WHEN (Acciones)", gbc);
        gbc.gridy = 7; panelEdicion.add(new JScrollPane(txtWhen), gbc);

        gbc.gridy = 8; addHeader(panelEdicion, "THEN (Resultados)", gbc);
        gbc.gridy = 9; panelEdicion.add(new JScrollPane(txtThen), gbc);

        gbc.weighty = 0;
        gbc.gridy = 10; addHeader(panelEdicion, "Estado Actual", gbc);
        gbc.gridy = 11; panelEdicion.add(comboEstado, gbc);
        
        //PANEL DE ADJUNTOS (DERECHA - LATERAL)
        JPanel panelAdjuntos = new JPanel(new BorderLayout(5, 5));
        panelAdjuntos.setBorder(BorderFactory.createTitledBorder("Evidencias / Adjuntos"));
        panelAdjuntos.setPreferredSize(new Dimension(300, 0));
        
        modelAdjuntos = new DefaultListModel<>();
        listAdjuntos = new JList<>(modelAdjuntos);
        listAdjuntos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAdjuntos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Listener Doble Clic para abrir adjunto
        listAdjuntos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    descargarYAbrirArchivo();
                }
            }
        });

        JScrollPane scrollAdjuntos = new JScrollPane(listAdjuntos);
        
        JButton btnVer = new JButton("👁️ Ver");
        JButton btnAdjuntar = new JButton("📎 Agregar");
        JButton btnEliminarAdjunto = new JButton("🗑️ Quitar");
        
        JPanel pnlAccionesAdj = new JPanel(new GridLayout(1, 3, 5, 5)); 
        pnlAccionesAdj.add(btnAdjuntar);
        pnlAccionesAdj.add(btnVer);
        pnlAccionesAdj.add(btnEliminarAdjunto);

        panelAdjuntos.add(scrollAdjuntos, BorderLayout.CENTER);
        panelAdjuntos.add(pnlAccionesAdj, BorderLayout.SOUTH);

        //PANEL DE PREVIEW (CENTRO)
        JPanel panelPreview = new JPanel(new BorderLayout());
        panelPreview.setBorder(BorderFactory.createTitledBorder("Feature File Preview"));
        panelPreview.add(new JScrollPane(txtPreview));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelEdicion, panelPreview);
        split.setDividerLocation(500);
        split.setResizeWeight(0.5);

        //BOTONES (ABAJO)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton btnExportar = new JButton("Exportar .feature");
        JButton btnGuardar = new JButton("Guardar Cambios");
        
        btnGuardar.putClientProperty(FlatClientProperties.STYLE, 
                "background: $Component.accentColor; foreground: #ffffff; font: bold");

        // Action Listeners de botones
        btnGuardar.addActionListener(e -> guardarCambios());
        btnExportar.addActionListener(e -> exportarFeature());
        btnAdjuntar.addActionListener(e -> adjuntarArchivo());
        btnVer.addActionListener(e -> descargarYAbrirArchivo());
        btnEliminarAdjunto.addActionListener(e -> eliminarAdjuntoSeleccionado());
        
        panelBotones.add(btnExportar);
        panelBotones.add(btnGuardar);

        add(split, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
        add(panelAdjuntos, BorderLayout.EAST);
    }
    
    private void eliminarAdjuntoSeleccionado() {
        String nombreSeleccionado = listAdjuntos.getSelectedValue();
        if (nombreSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar archivo '" + nombreSeleccionado + "'?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            adjuntoDAO.eliminar(nombreSeleccionado, caso.getId());
            cargarListaAdjuntos();
        }
    }

	private void cargarListaAdjuntos() {
        if (modelAdjuntos == null) return;
        modelAdjuntos.clear();
        List<String> nombres = adjuntoDAO.listarNombresPorCaso(caso.getId());
        for (String nombre : nombres) {
            modelAdjuntos.addElement(nombre);
        }
    }

    private void adjuntarArchivo() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            try {
                byte[] contenido = Files.readAllBytes(archivo.toPath());
                Adjunto nuevoAdjunto = new Adjunto(0, caso.getId(), archivo.getName(), contenido);
                adjuntoDAO.guardar(nuevoAdjunto);
                cargarListaAdjuntos();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer archivo: " + ex.getMessage());
            }
        }
    }
    
    private void descargarYAbrirArchivo() {
        String nombreSeleccionado = listAdjuntos.getSelectedValue();
        if (nombreSeleccionado == null) return;

        try {
            Adjunto adjunto = adjuntoDAO.obtenerPorNombreYCaso(nombreSeleccionado, caso.getId());
            if (adjunto != null && adjunto.getContenido() != null) {
                String prefix = "evidencia_";
                String suffix = nombreSeleccionado.contains(".") ? 
                                nombreSeleccionado.substring(nombreSeleccionado.lastIndexOf(".")) : ".tmp";
                
                File tempFile = File.createTempFile(prefix, suffix);
                Files.write(tempFile.toPath(), adjunto.getContenido());
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(tempFile);
                }
                tempFile.deleteOnExit();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al abrir: " + ex.getMessage());
        }
    }

    private void addHeader(JPanel p, String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(UIManager.getColor("Label.disabledForeground"));
        gbc.insets = new Insets(12, 0, 2, 0);
        p.add(label, gbc);
        gbc.insets = new Insets(0, 0, 8, 0);
    }

    private void estilizarArea(JTextArea area, String placeholder) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
    }

    private void generarPreview() {
        txtPreview.setText(""); 
        String given = formatearStep("Given", txtGiven.getText());
        String when  = formatearStep("When", txtWhen.getText());
        String then  = formatearStep("Then", txtThen.getText());

        appendColoredText("Feature: ", new Color(133, 153, 0)); 
        appendColoredText(caso.getNombre() + "\n\n", null);
        
        appendColoredText("  Scenario: ", new Color(38, 139, 210)); 
        appendColoredText(txtNombre.getText() + "\n\n", null);

        if (!given.isEmpty()) insertStepInPreview(given, new Color(181, 137, 0));
        if (!when.isEmpty()) insertStepInPreview(when, new Color(211, 54, 130));
        if (!then.isEmpty()) insertStepInPreview(then, new Color(42, 161, 152));
    }

    private void insertStepInPreview(String content, Color color) {
        String[] lineas = content.split("\n");
        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;
            String[] partes = linea.trim().split(" ", 2);
            if (partes.length >= 1) {
                appendColoredText("    " + partes[0] + " ", color);
                if (partes.length > 1) appendColoredText(partes[1], null);
                appendColoredText("\n", null);
            }
        }
    }

    private void appendColoredText(String text, Color color) {
        StyledDocument doc = txtPreview.getStyledDocument();
        Style style = txtPreview.addStyle("Style", null);
        if (color != null) StyleConstants.setForeground(style, color);
        else StyleConstants.setForeground(style, UIManager.getColor("TextPane.foreground"));
        try { doc.insertString(doc.getLength(), text, style); } catch (BadLocationException ignored) {}
    }

    private String formatearStep(String keyword, String texto) { 
        texto = texto.trim();
        if (texto.isEmpty()) return "";
        String[] lineas = texto.split("\\n");
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) continue;
            linea = linea.substring(0,1).toUpperCase() + linea.substring(1);
            if (i == 0) {
                resultado.append(linea.startsWith(keyword) ? linea : keyword + " " + linea);
            } else {
                resultado.append(linea.startsWith("And") ? "\n" + linea : "\nAnd " + linea);
            }
        }
        return resultado.toString();
    }

    private void guardarCambios() {
        Estado estadoSeleccionado = (Estado) comboEstado.getSelectedItem();
        if (estadoSeleccionado == Estado.PASADO && (txtGiven.getText().isEmpty() || txtWhen.getText().isEmpty() || txtThen.getText().isEmpty())) {
            JOptionPane.showMessageDialog(this, "Complete todos los pasos para terminar el caso.");
            return;
        }

        caso.setNombre(txtNombre.getText().trim());
        caso.setCriterioAceptacion(txtCriterio.getText().trim());
        caso.setGivenStep(formatearStep("Given", txtGiven.getText()));
        caso.setWhenStep(formatearStep("When", txtWhen.getText()));
        caso.setThenStep(formatearStep("Then", txtThen.getText()));
        caso.setEstado(estadoSeleccionado);

        casoDAO.actualizarCaso(caso);
        if (panelPadre != null) panelPadre.cargarCasos();
        dispose();
    }

    private void exportarFeature() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(caso.getNombre().replaceAll(" ", "_") + ".feature"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("Feature: " + caso.getNombre());
                writer.println("\n  Scenario: " + txtNombre.getText());
                writer.println("    " + txtGiven.getText().replace("\n", "\n    "));
                writer.println("    " + txtWhen.getText().replace("\n", "\n    "));
                writer.println("    " + txtThen.getText().replace("\n", "\n    "));
                JOptionPane.showMessageDialog(this, "Exportado con éxito");
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void agregarAutoPreview() {
        javax.swing.event.DocumentListener listener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { generarPreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { generarPreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { generarPreview(); }
        };
        txtNombre.getDocument().addDocumentListener(listener);
        txtGiven.getDocument().addDocumentListener(listener);
        txtWhen.getDocument().addDocumentListener(listener);
        txtThen.getDocument().addDocumentListener(listener);
    }
}