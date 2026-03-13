package dao;

import model.CasoPrueba;
import model.Estado;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CasoPruebaDAO {

    public void crearCaso(CasoPrueba caso) {

        String sql = "INSERT INTO caso_prueba(nombre,descripcion,estado,escenario_id, criterio_aceptacion, given_step, when_step, then_step) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, caso.getNombre());
            ps.setString(2, caso.getDescripcion());
            ps.setString(3, caso.getEstado().name());
            ps.setInt(4, caso.getEscenarioId());
            ps.setString(5, caso.getCriterioAceptacion());
            ps.setString(6, caso.getGivenStep());
            ps.setString(7, caso.getWhenStep());
            ps.setString(8, caso.getThenStep());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CasoPrueba> listarPorEscenario(int escenarioId) {

        List<CasoPrueba> lista = new ArrayList<>();
        String sql = "SELECT * FROM caso_prueba WHERE escenario_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, escenarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new CasoPrueba(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        Estado.valueOf(rs.getString("estado")),
                        rs.getInt("escenario_id"),
                        rs.getString("criterio_aceptacion"),
                        rs.getString("given_step"),
                        rs.getString("when_step"),
                        rs.getString("then_step")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void actualizarEstado(int id, Estado nuevoEstado) {

        String sql = "UPDATE caso_prueba SET estado=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarCaso(CasoPrueba caso) {

        String sql = """
            UPDATE caso_prueba
            SET nombre=?,
                given_step=?,
                when_step=?,
                then_step=?,
                estado=?
            WHERE id=?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, caso.getNombre());
            ps.setString(2, caso.getGivenStep());
            ps.setString(3, caso.getWhenStep());
            ps.setString(4, caso.getThenStep());
            ps.setString(5, caso.getEstado().name());
            ps.setInt(6, caso.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	 public void eliminarCaso(int id) {

		String sql = "DELETE FROM caso_prueba WHERE id=?";

		try (Connection con = DBConnection.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);
			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
    
    
		}
	 }
	 



}
