package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.formdev.flatlaf.FlatClientProperties;

import dao.CasoPruebaDAO;
import dao.EscenarioDAO;
import dao.ProyectoDAO;
import model.CasoPrueba;
import model.Escenario;
import model.Estado;
import model.Proyecto;
import model.Usuario;
import util.ReporteService;

@SuppressWarnings("serial")
public class ProyectoPanel extends JPanel {

	private Usuario usuario;
	private ProyectoDAO dao;
	private EscenarioDAO escenarioDAO = new EscenarioDAO();
	private CasoPruebaDAO casoDAO = new CasoPruebaDAO();

	private JTable table, tableEscenarios, tableCasos;
	private DefaultTableModel model, modelEscenarios, modelCasos;
	private List<CasoPrueba> listaCasos;

	private JTextField txtBuscarCaso;
	private TableRowSorter<DefaultTableModel> sorterCasos;

	private JLabel lblTotal, lblPasados, lblFallidos, lblPorcentaje;	
	private JProgressBar progressTest;

	public ProyectoPanel(Usuario usuario) {
		this.usuario = usuario;
		this.dao = new ProyectoDAO();

		initModels();
		initComponents();
		configurarListeners();
		cargarProyectos();
	}

	private void initModels() {
		model = new DefaultTableModel(new Object[] { "ID", "Nombre", "Descripción" }, 0) {
			@Override public boolean isCellEditable(int r, int c) { return false; }
		};

		modelEscenarios = new DefaultTableModel(new Object[] { "ID", "Nombre", "Descripción" }, 0) {
			@Override public boolean isCellEditable(int r, int c) { return false; }
		};

		modelCasos = new DefaultTableModel(new Object[] { "ID", "Nombre", "Estado" }, 0) {
			@Override public boolean isCellEditable(int r, int c) { return false; }
		};
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// --- BÚSQUEDA ---
		txtBuscarCaso = new JTextField(15);
		txtBuscarCaso.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🔍 Buscar caso...");
		txtBuscarCaso.putClientProperty(FlatClientProperties.STYLE, "arc: 20");

		// --- TABLAS ---
		table = new JTable(model);
		tableEscenarios = new JTable(modelEscenarios);
		tableCasos = new JTable(modelCasos);

		estilizarTabla(table);
		estilizarTabla(tableEscenarios);
		estilizarTabla(tableCasos);
		
		ocultarID(table);
		ocultarID(tableEscenarios);
		ocultarID(tableCasos);
		
		configurarRenderizadorEstado();

		// --- TOOLBAR ---
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

		JButton btnNuevo = new JButton("Nuevo Proyecto");
		JButton btnEditar = new JButton("Editar Proyecto");
		JButton btnDelProy = new JButton("Eliminar Proyecto");

		JButton btnNuevoEscenario = new JButton("Añadir Escenario");
		JButton btnEditarEscenario = new JButton("Editar Escenario");
		JButton btnDelEsc = new JButton("Eliminar Escenario");

		JButton btnNuevoCaso = new JButton("Crear Test Case");
		JButton btnDelCaso = new JButton("Eliminar Caso");

		JButton btnExportar = new JButton("Exportar Feature");
		JButton btnMetricas = new JButton("📈 Ver Métricas");
		btnMetricas.putClientProperty(FlatClientProperties.STYLE, "font: bold");
		
		JButton btnReporte = new JButton("📊 Reporte PDF");
		btnReporte.putClientProperty(FlatClientProperties.STYLE, "foreground: #2d6a4f; font: bold");
		
		JButton btnCerrarSesion = new JButton("🚪 Cerrar Sesión");
		btnCerrarSesion.putClientProperty(FlatClientProperties.STYLE, "foreground: #db3a34; font: bold");
		
		JButton btnGestionUsuarios = new JButton("Gestionar Usuarios");
		btnGestionUsuarios.setVisible(usuario.isAdmin());
		
		btnGestionUsuarios.putClientProperty(FlatClientProperties.STYLE, 
			    "background: #268bd2; " + 
			    "foreground: #ffffff; " + 
			    "font: bold; " + 
			    "arc: 10");

		// Componer Toolbar
		toolBar.add(btnNuevo);
		toolBar.add(btnEditar);
		toolBar.add(btnDelProy);
		toolBar.addSeparator();
		toolBar.add(btnNuevoEscenario);
		toolBar.add(btnEditarEscenario);
		toolBar.add(btnDelEsc);
		toolBar.addSeparator();
		toolBar.add(btnNuevoCaso);
		toolBar.add(btnDelCaso);
		toolBar.addSeparator();
		toolBar.add(btnReporte);
		toolBar.add(btnMetricas);
		toolBar.add(btnExportar);
		toolBar.addSeparator();
		toolBar.add(btnGestionUsuarios);
		
		toolBar.addSeparator();
		toolBar.add(new JLabel("Filtrar: "));
		toolBar.add(txtBuscarCaso);
		toolBar.add(Box.createHorizontalGlue());
		toolBar.add(btnCerrarSesion);

		// --- PANEL DE ESTADÍSTICAS ---
		JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 8));
		panelStats.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

		lblTotal = new JLabel("Total: 0");
		lblPasados = new JLabel("✔ Pasados: 0");
		lblPasados.setForeground(new Color(0, 120, 0));
		lblFallidos = new JLabel("✘ Fallidos: 0");
		lblFallidos.setForeground(new Color(200, 30, 0));
		lblPorcentaje = new JLabel("0% Calidad");
		
		progressTest = new JProgressBar(0, 100);
		progressTest.setPreferredSize(new Dimension(180, 18));
		progressTest.setStringPainted(true);

		panelStats.add(lblTotal);
		panelStats.add(lblPasados);
		panelStats.add(lblFallidos);
		panelStats.add(new JLabel("|"));
		panelStats.add(lblPorcentaje);
		panelStats.add(progressTest);

		// --- LAYOUT PRINCIPAL ---
		JScrollPane spProy = new JScrollPane(table);
		spProy.setBorder(BorderFactory.createTitledBorder("Mis Proyectos"));
		JScrollPane spEsc = new JScrollPane(tableEscenarios);
		spEsc.setBorder(BorderFactory.createTitledBorder("Escenarios"));

		JSplitPane splitIzquierdo = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spProy, spEsc);
		splitIzquierdo.setDividerLocation(300);

		JScrollPane spCasos = new JScrollPane(tableCasos);
		spCasos.setBorder(BorderFactory.createTitledBorder("Casos de Prueba"));

		JPanel panelDerecho = new JPanel(new BorderLayout());
		panelDerecho.add(spCasos, BorderLayout.CENTER);
		panelDerecho.add(panelStats, BorderLayout.SOUTH);

		JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitIzquierdo, panelDerecho);
		splitPrincipal.setDividerLocation(500);

		add(toolBar, BorderLayout.NORTH);
		add(splitPrincipal, BorderLayout.CENTER);

		// LISTENERS BOTONES
		btnNuevo.addActionListener(e -> crearProyecto());
		btnEditar.addActionListener(e -> editarProyecto());
		btnDelProy.addActionListener(e -> eliminarProyecto());
		btnNuevoEscenario.addActionListener(e -> crearEscenario());
		btnEditarEscenario.addActionListener(e -> editarEscenario());
		btnDelEsc.addActionListener(e -> eliminarEscenario());
		btnNuevoCaso.addActionListener(e -> crearCaso());
		btnDelCaso.addActionListener(e -> eliminarCaso());
		btnExportar.addActionListener(e -> exportarFeature());
		btnReporte.addActionListener(e -> exportarReportePDF());
		btnMetricas.addActionListener(e -> abrirMetricas());
		btnCerrarSesion.addActionListener(e -> cerrarSesion());
		btnGestionUsuarios.addActionListener(e -> abrirGestionUsuarios());
	}

	private void abrirGestionUsuarios() {
	    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Administración de Usuarios", true);
	    dialog.setSize(700, 450);
	    dialog.setLocationRelativeTo(this);
	    
	    // LÓGICA PARA SALIR CON ESCAPE
	    KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
	    Action escapeAction = new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            dialog.dispose(); // Cierra el diálogo al presionar ESC
	        }
	    };
	    
	    // Registramos la acción en el panel raíz del diálogo
	    dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
	    dialog.getRootPane().getActionMap().put("ESCAPE", escapeAction);
	    // ------------------------------------

	    dialog.add(new UsuariosAdminPanel()); 
	    dialog.setVisible(true);
	}

	private void ocultarID(JTable tabla) {
		tabla.getColumnModel().getColumn(0).setMinWidth(0);
		tabla.getColumnModel().getColumn(0).setMaxWidth(0);
		tabla.getColumnModel().getColumn(0).setWidth(0);
	}
	
	private void configurarListeners() {
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) cargarEscenarios();
		});

		tableEscenarios.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) cargarCasos();
		});

		sorterCasos = new TableRowSorter<>(modelCasos);
		tableCasos.setRowSorter(sorterCasos);

		tableCasos.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					int filaVista = tableCasos.getSelectedRow();
					if (filaVista != -1) {
						int filaModelo = tableCasos.convertRowIndexToModel(filaVista);
						CasoPrueba casoSeleccionado = listaCasos.get(filaModelo);
						new GherkinEditorFrame(casoSeleccionado, ProyectoPanel.this).setVisible(true);
					}
				}
			}
		});

		txtBuscarCaso.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
			public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
			private void filtrar() {
				String texto = txtBuscarCaso.getText();
				if (texto.trim().length() == 0) sorterCasos.setRowFilter(null);
				else sorterCasos.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 1));
			}
		});
	}

	// --- LÓGICA DE REPORTES ---

	private void exportarReportePDF() {
		int fila = tableEscenarios.getSelectedRow();
		if (fila < 0) {
			JOptionPane.showMessageDialog(this, "Seleccione un escenario primero.");
			return;
		}

		int idEsc = (int) modelEscenarios.getValueAt(fila, 0);
		String nombreEsc = (String) modelEscenarios.getValueAt(fila, 1);
		String descEsc = (String) modelEscenarios.getValueAt(fila, 2);
		
		// Obtener ID proyecto de la tabla principal
		int proyId = (int) model.getValueAt(table.getSelectedRow(), 0);
		Escenario escenario = new Escenario(idEsc, nombreEsc, descEsc, proyId);
		List<CasoPrueba> casos = casoDAO.listarPorEscenario(idEsc);

		LocalDateTime ahora = LocalDateTime.now();
		String fechaTxt = ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
		String fechaArch = ahora.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));

		JFileChooser chooser = new JFileChooser();
		chooser.setSelectedFile(new File("Reporte_" + nombreEsc.replaceAll(" ", "_") + "_" + fechaArch + ".pdf"));

		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			ReporteService service = new ReporteService();
			service.generarReporteEscenario(escenario, casos, fechaTxt, chooser.getSelectedFile().getAbsolutePath());
			JOptionPane.showMessageDialog(this, "Reporte PDF detallado generado.");
		}
	}

	private void abrirMetricas() {
		int fila = tableEscenarios.getSelectedRow();
		if (fila >= 0) {
			int idEsc = (int) modelEscenarios.getValueAt(fila, 0);
			String nombreEsc = (String) modelEscenarios.getValueAt(fila, 1);
			List<CasoPrueba> casos = casoDAO.listarPorEscenario(idEsc);
			
			if (casos.isEmpty()) {
				JOptionPane.showMessageDialog(this, "El escenario no tiene casos.");
				return;
			}
			new ReporteMetricasFrame(nombreEsc, casos).setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "Seleccione un escenario para ver métricas.");
		}
	}

	//CARGA DE DATOS

	public void cargarProyectos() {
	    model.setRowCount(0);
	    List<Proyecto> proyectos;

	    if (usuario.isAdmin()) {
	        // Si es admin, usa el nuevo método para traer todo
	        proyectos = dao.listarTodo();
	        
	        this.setBorder(BorderFactory.createTitledBorder("Panel de Administración - Todos los Proyectos"));
	    } else {
	        // Si es usuario común, solo ve los suyos
	        proyectos = dao.listarPorUsuario(usuario.getId());
	    }

	    for (Proyecto p : proyectos) {
	        model.addRow(new Object[] { p.getId(), p.getNombre(), p.getDescripcion() });
	    }
	}

	private void cargarEscenarios() {
		int fila = table.getSelectedRow();
		modelEscenarios.setRowCount(0);
		modelCasos.setRowCount(0);
		if (fila >= 0) {
			int proyectoId = (int) model.getValueAt(fila, 0);
			for (Escenario esc : escenarioDAO.listarPorProyecto(proyectoId)) {
				modelEscenarios.addRow(new Object[] { esc.getId(), esc.getNombre(), esc.getDescripcion() });
			}
		}
		actualizarEstadisticas();
	}

	public void cargarCasos() {
		int fila = tableEscenarios.getSelectedRow();
		modelCasos.setRowCount(0);
		if (fila >= 0) {
			int escenarioId = (int) modelEscenarios.getValueAt(fila, 0);
			listaCasos = casoDAO.listarPorEscenario(escenarioId);
			for (CasoPrueba c : listaCasos) {
				modelCasos.addRow(new Object[] { c.getId(), c.getNombre(), c.getEstado() });
			}
		}
		actualizarEstadisticas();
	}

	private void actualizarEstadisticas() {
		if (listaCasos == null || listaCasos.isEmpty()) {
			lblTotal.setText("Total: 0"); lblPasados.setText("✔ Pasados: 0");
			lblFallidos.setText("✘ Fallidos: 0"); lblPorcentaje.setText("0% Calidad");
			progressTest.setValue(0); return;
		}
		int total = listaCasos.size();
		long countPasado = listaCasos.stream().filter(c -> c.getEstado() == Estado.PASADO).count();
		long countFallido = listaCasos.stream().filter(c -> c.getEstado() == Estado.FALLIDO).count();
		int porcentaje = (int) ((countPasado * 100) / total);
		
		lblTotal.setText("Total: " + total);
		lblPasados.setText("✔ Pasados: " + countPasado);
		lblFallidos.setText("✘ Fallidos: " + countFallido);
		lblPorcentaje.setText(porcentaje + "% Calidad" + (countFallido > 0 ? " (Revisar Fallos)" : ""));
		lblPorcentaje.setForeground(countFallido > 0 ? new Color(200, 10, 0) : UIManager.getColor("Label.foreground"));
		progressTest.setValue(porcentaje);
	}

	// CRUD MÉTODOS

	private void crearProyecto() {
		ProyectoDialog dialog = new ProyectoDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Proyecto", null);
		dialog.setVisible(true);
		if (dialog.isGuardado()) {
			dao.crearProyecto(new Proyecto(dialog.getNombre(), dialog.getDescripcion(), usuario.getId()));
			cargarProyectos();
		}
	}

	private void editarProyecto() {
		int f = table.getSelectedRow();
		if (f >= 0) {
			Proyecto p = new Proyecto((int)model.getValueAt(f,0), (String)model.getValueAt(f,1), (String)model.getValueAt(f,2), usuario.getId());
			ProyectoDialog dialog = new ProyectoDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Proyecto", p);
			dialog.setVisible(true);
			if (dialog.isGuardado()) {
				dao.actualizarProyecto(new Proyecto(p.getId(), dialog.getNombre(), dialog.getDescripcion(), usuario.getId()));
				cargarProyectos();
			}
		}
	}

	private void eliminarProyecto() {
		int f = table.getSelectedRow();
		if (f >= 0 && JOptionPane.showConfirmDialog(this, "¿Eliminar proyecto?") == JOptionPane.YES_OPTION) {
			dao.eliminarProyecto((int) model.getValueAt(f, 0));
			cargarProyectos();
		}
	}

	private void crearEscenario() {
		int f = table.getSelectedRow();
		if (f >= 0) {
			EscenarioDialog d = new EscenarioDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Escenario", null);
			d.setVisible(true);
			if (d.isGuardado()) {
				escenarioDAO.crearEscenario(new Escenario(d.getNombre(), d.getDescripcion(), (int) model.getValueAt(f, 0)));
				cargarEscenarios();
			}
		}
	}

	private void editarEscenario() {
		int f = tableEscenarios.getSelectedRow();
		if (f >= 0) {
			Escenario esc = new Escenario((int)modelEscenarios.getValueAt(f,0), (String)modelEscenarios.getValueAt(f,1), (String)modelEscenarios.getValueAt(f,2), (int)model.getValueAt(table.getSelectedRow(), 0));
			EscenarioDialog d = new EscenarioDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Escenario", esc);
			d.setVisible(true);
			if (d.isGuardado()) {
				escenarioDAO.actualizarEscenario(new Escenario(esc.getId(), d.getNombre(), d.getDescripcion(), esc.getProyectoId()));
				cargarEscenarios();
			}
		}
	}

	private void eliminarEscenario() {
		int f = tableEscenarios.getSelectedRow();
		if (f >= 0 && JOptionPane.showConfirmDialog(this, "¿Eliminar escenario?") == JOptionPane.YES_OPTION) {
			escenarioDAO.eliminarEscenario((int) modelEscenarios.getValueAt(f, 0));
			cargarEscenarios();
		}
	}

	private void crearCaso() {
		int f = tableEscenarios.getSelectedRow();
		if (f >= 0) {
			CasoPruebaDialog d = new CasoPruebaDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Caso");
			d.setVisible(true);
			CasoPrueba c = d.getCaso((int) modelEscenarios.getValueAt(f, 0));
			if (c != null) {
				casoDAO.crearCaso(c);
				cargarCasos();
			}
		}
	}

	private void eliminarCaso() {
		int f = tableCasos.getSelectedRow();
		if (f >= 0 && JOptionPane.showConfirmDialog(this, "¿Eliminar caso?") == JOptionPane.YES_OPTION) {
			casoDAO.eliminarCaso((int) modelCasos.getValueAt(f, 0));
			cargarCasos();
		}
	}

	private void exportarFeature() {
		int f = tableEscenarios.getSelectedRow();
		if (f >= 0) {
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new File(modelEscenarios.getValueAt(f, 1).toString().replaceAll(" ", "_") + ".feature"));
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				try (java.io.PrintWriter w = new java.io.PrintWriter(chooser.getSelectedFile())) {
					w.println("Feature: " + modelEscenarios.getValueAt(f, 1));
					for (CasoPrueba c : casoDAO.listarPorEscenario((int) modelEscenarios.getValueAt(f, 0))) {
						w.println("\n  Scenario: " + c.getNombre());
						w.println("    Given " + c.getGivenStep());
						w.println("    When " + c.getWhenStep());
						w.println("    Then " + c.getThenStep());
					}
					JOptionPane.showMessageDialog(this, "Feature Gherkin exportada.");
				} catch (Exception ex) { ex.printStackTrace(); }
			}
		}
	}

	private void cerrarSesion() {
		if (JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			SwingUtilities.getWindowAncestor(this).dispose();
			new LoginFrame().setVisible(true);
		}
	}

	// ESTILOS

	private void estilizarTabla(JTable tabla) {
		tabla.setRowHeight(35);
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabla.getTableHeader().setReorderingAllowed(false);
		tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		if (tabla.getModel() == modelCasos) {
			tabla.getColumnModel().getColumn(1).setPreferredWidth(450); 
			tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
			tabla.getColumnModel().getColumn(2).setMaxWidth(150);
		} else if (tabla.getColumnCount() >= 3) {
			tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
			tabla.getColumnModel().getColumn(2).setPreferredWidth(400);
		}
	}

	private void configurarRenderizadorEstado() {
		tableCasos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setForeground(UIManager.getColor("Table.foreground"));
				if (column == 2 && value != null) {
					setHorizontalAlignment(JLabel.CENTER);
					if (!isSelected) {
						String est = value.toString();
						if (est.equals("PASADO")) c.setForeground(new Color(0, 150, 0));
						else if (est.equals("FALLIDO")) c.setForeground(new Color(200, 0, 0));
						else if (est.equals("BLOQUEADO")) c.setForeground(new Color(150, 0, 150));
						else if (est.equals("EJECUTANDO")) c.setForeground(new Color(0, 100, 200));
						else if (est.equals("LISTO")) c.setForeground(new Color(200, 120, 0));
					}
				} else { setHorizontalAlignment(JLabel.LEFT); }
				if (column == 1 && !isSelected) c.setForeground(Color.WHITE);
				return c;
			}
		});
	}
}