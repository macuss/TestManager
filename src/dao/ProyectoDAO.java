package dao;

import model.Proyecto;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDAO {

    public void crearProyecto(Proyecto proyecto) {

        String sql = "INSERT INTO proyecto(nombre,descripcion,usuario_id) VALUES(?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, proyecto.getNombre());
            ps.setString(2, proyecto.getDescripcion());
            ps.setInt(3, proyecto.getUsuarioId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Proyecto> listarPorUsuario(int usuarioId) { // Lista solo los proyectos del usuario logueado

        List<Proyecto> lista = new ArrayList<>();
        String sql = "SELECT * FROM proyecto WHERE usuario_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Proyecto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("usuario_id")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    // para user admin, listamos todos los proyectos
    public List<Proyecto> listarTodo() {
        List<Proyecto> lista = new ArrayList<>();
        String sql = "SELECT * FROM proyecto";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Proyecto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("usuario_id")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    
    public void eliminarProyecto(int id) {

        String sql = "DELETE FROM proyecto WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void actualizarProyecto(Proyecto proyecto) {

        String sql = "UPDATE proyecto SET nombre=?, descripcion=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, proyecto.getNombre());
            ps.setString(2, proyecto.getDescripcion());
            ps.setInt(3, proyecto.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
}
