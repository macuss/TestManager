package dao;

import util.DBConnection;
import model.Adjunto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdjuntoDAO {


    public void guardar(Adjunto adj) {
        String sql = "INSERT INTO adjuntos (caso_id, nombre_archivo, contenido, tamanio) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, adj.getCasoId());
            ps.setString(2, adj.getNombre());
            ps.setBytes(3, adj.getContenido());
            ps.setLong(4, adj.getContenido() != null ? adj.getContenido().length : 0);
            
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al guardar adjunto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    
     //solo los nombres de los archivos para mostrarlos en la lista visual
     //sin traer todo a la memoria (parece más eficiente).. 
	 
    public List<String> listarNombresPorCaso(int casoId) {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT nombre_archivo FROM adjuntos WHERE caso_id = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, casoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    nombres.add(rs.getString("nombre_archivo"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nombres;
    }


    
    public Adjunto obtenerPorNombreYCaso(String nombre, int casoId) {
        String sql = "SELECT * FROM adjuntos WHERE nombre_archivo = ? AND caso_id = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            ps.setInt(2, casoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Adjunto(
                        rs.getInt("id"),
                        rs.getInt("caso_id"),
                        rs.getString("nombre_archivo"),
                        rs.getBytes("contenido")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void eliminar(String nombre, int casoId) {
        String sql = "DELETE FROM adjuntos WHERE nombre_archivo = ? AND caso_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            ps.setInt(2, casoId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}