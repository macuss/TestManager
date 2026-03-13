package view;

import dao.UsuarioDAO;
import model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class UsuariosAdminPanel extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public UsuariosAdminPanel() {
        setLayout(new BorderLayout());
        
        modelo = new DefaultTableModel(new Object[]{"ID", "Alias", "Nombre", "Rol", "Legajo"}, 0);
        tabla = new JTable(modelo);
        
        cargarUsuarios();

        JButton btnEliminar = new JButton("Dar de Baja (Eliminar)");
        JButton btnCambiarRol = new JButton("Cambiar Rol (Admin/Tester)");

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        //add(btnEliminar, BorderLayout.SOUTH);
        
        
        
        
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnCambiarRol);
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);
        
        
        //listeners
        btnCambiarRol.addActionListener(e -> alternarRolUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
    }

    private void alternarRolUsuario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la lista.");
            return;
        }

        int id = (int) modelo.getValueAt(fila, 0);
        String alias = (String) modelo.getValueAt(fila, 1);
        String rolActual = (String) modelo.getValueAt(fila, 3);
        
      
        String nuevoRol = rolActual.equalsIgnoreCase("admin") ? "tester" : "admin";

        int confirmar = JOptionPane.showConfirmDialog(this, 
            "¿Desea cambiar el rol de '" + alias + "' a " + nuevoRol.toUpperCase() + "?");

        if (confirmar == JOptionPane.YES_OPTION) {
            usuarioDAO.cambiarRol(id, nuevoRol);
            JOptionPane.showMessageDialog(this, "Rol actualizado con éxito.");
            cargarUsuarios(); // Refresca tabla
        }
    }

    
	private void cargarUsuarios() {
        modelo.setRowCount(0);
     
        List<Usuario> lista = usuarioDAO.listarTodos(); 
        for (Usuario u : lista) {
            modelo.addRow(new Object[]{u.getId(), u.getAlias(), u.getNombre(), u.getRol(), u.getLegajo()});
        }
    }

    private void eliminarUsuario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        
        int id = (int) modelo.getValueAt(fila, 0);
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este usuario?");
        
        if (confirmar == JOptionPane.YES_OPTION) {
       
            usuarioDAO.eliminar(id); 
            cargarUsuarios();
        }
        
    }
    
    
    
    
}