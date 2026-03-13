package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import model.Usuario;
import util.DBConnection;

public class UsuarioDAO {

    public void registrar(Usuario usuario) {
    
        String sql = "INSERT INTO usuario(alias, nombre, apellido, legajo, password, rol) VALUES(?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getAlias());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido()); 
            ps.setString(4, usuario.getLegajo());
            ps.setString(5, usuario.getPassword());
            ps.setString(6, usuario.getRol()); // (por defecto "tester")

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Usuario login(String alias, String password) {
        String sql = "SELECT * FROM usuario WHERE alias=? AND password=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, alias);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("alias"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("legajo"),
                    rs.getString("password"),
                    rs.getString("rol") 
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("id"), rs.getString("alias"), rs.getString("nombre"), 
                                      rs.getString("apellido"), rs.getString("legajo"), 
                                      rs.getString("password"), rs.getString("rol")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public void cambiarRol(int id, String nuevoRol) {
        String sql = "UPDATE usuario SET rol = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nuevoRol);
            ps.setInt(2, id);
            ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}