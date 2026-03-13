package view;

import com.formdev.flatlaf.FlatClientProperties;
import model.CasoPrueba;
import model.Estado;
import util.ReporteService; // Asegúrate de que esta clase exista

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("serial")
public class ReporteMetricasFrame extends JFrame {

    public ReporteMetricasFrame(String nombreEscenario, List<CasoPrueba> casos) {
        setTitle("Métricas: " + nombreEscenario);
        setSize(500, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout()); // BorderLayout para separar el scroll del botón fijo

        // CÁLCULOS
        int total = casos.size();
        long pasados = casos.stream().filter(c -> c.getEstado() == Estado.PASADO).count();
        long fallidos = casos.stream().filter(c -> c.getEstado() == Estado.FALLIDO).count();
        long bloqueados = casos.stream().filter(c -> c.getEstado() == Estado.BLOQUEADO).count();
        double porcentajePasados = total > 0 ? (pasados * 100.0) / total : 0;

        // PANEL DE CONTENIDO (CON SCROLL)
        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tarjeta Principal
        mainPanel.add(crearCardMetrica("Calidad General", String.format("%.1f%%", porcentajePasados), 
                      porcentajePasados > 80 ? new Color(40, 167, 69) : new Color(220, 53, 69)));

        // Grilla Secundaria
        JPanel gridSecundario = new JPanel(new GridLayout(2, 2, 10, 10));
        gridSecundario.add(crearCardMetrica("Total Tests", String.valueOf(total), UIManager.getColor("Label.foreground")));
        gridSecundario.add(crearCardMetrica("Pasados", String.valueOf(pasados), new Color(40, 167, 69)));
        gridSecundario.add(crearCardMetrica("Fallidos", String.valueOf(fallidos), new Color(220, 53, 69)));
        gridSecundario.add(crearCardMetrica("Bloqueados", String.valueOf(bloqueados), new Color(102, 16, 242)));
        mainPanel.add(gridSecundario);

        // Barra de progreso
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) porcentajePasados);
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(0, 30));
        
        JPanel pnlBarra = new JPanel(new BorderLayout(0, 10));
        pnlBarra.add(new JLabel("Distribución de Éxito:"), BorderLayout.NORTH);
        pnlBarra.add(bar, BorderLayout.CENTER);
        mainPanel.add(pnlBarra);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        //BOTÓN DE EXPORTACIÓN (FIJO ABAJO)
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnExportar = new JButton("📄 Exportar Reporte con Fecha");
        btnExportar.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: $Component.accentColor; foreground: #ffffff");
        btnExportar.setPreferredSize(new Dimension(300, 40));
        
        panelBoton.add(btnExportar);
        add(panelBoton, BorderLayout.SOUTH);

        //LÓGICA DE EXPORTACIÓN
        btnExportar.addActionListener(e -> {
            //Obtener fechas
            LocalDateTime ahora = LocalDateTime.now();
            String fechaTexto = ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            String fechaArchivo = ahora.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));

            //Preparar JFileChooser
            JFileChooser chooser = new JFileChooser();
            String nombreSugerido = "Metricas_" + nombreEscenario.replaceAll(" ", "_") + "_" + fechaArchivo + ".pdf";
            chooser.setSelectedFile(new File(nombreSugerido));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                ReporteService service = new ReporteService();
            
                service.generarReporteConMetricas(
                    nombreEscenario, 
                    casos, 
                    fechaTexto, 
                    chooser.getSelectedFile().getAbsolutePath()
                );
                JOptionPane.showMessageDialog(this, "Reporte exportado correctamente.");
            }
        });
        
  
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), "cerrarVentana");

        this.getRootPane().getActionMap().put("cerrarVentana", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        
    }

    private JPanel crearCardMetrica(String titulo, String valor, Color colorValor) {
        JPanel card = new JPanel(new BorderLayout());
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: $Menu.background");
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblVal.setForeground(colorValor);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        return card;
    }
}