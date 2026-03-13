package dao;

import model.Escenario;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EscenarioDAO {

    public void crearEscenario(Escenario escenario) {

        String sql = "INSERT INTO escenario(nombre,descripcion,proyecto_id) VALUES(?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, escenario.getNombre());
            ps.setString(2, escenario.getDescripcion());
            ps.setInt(3, escenario.getProyectoId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Escenario> listarPorProyecto(int proyectoId) {

        List<Escenario> lista = new ArrayList<>();
        String sql = "SELECT * FROM escenario WHERE proyecto_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, proyectoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Escenario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("proyecto_id")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void eliminarEscenario(int id) {
 
        String sql = "DELETE FROM escenario WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
            
        } catch (Exception e) {
            System.err.println("Error al eliminar escenario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarEscenario(Escenario esc) {
        String sql = "UPDATE escenario SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, esc.getNombre());
            ps.setString(2, esc.getDescripcion());
            ps.setInt(3, esc.getId());
            ps.executeUpdate();
            
        } catch (Exception e) { 
            System.err.println("Error al actualizar escenario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
