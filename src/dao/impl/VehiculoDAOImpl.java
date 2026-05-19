package dao.impl;

import dao.IVehiculoDAO;
import db.ConexionDB;
import model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAOImpl implements IVehiculoDAO {

    @Override
    public void insertar(Vehiculo v) {
        String sql = "INSERT INTO vehiculos (matricula,marca,modelo,anio,categoria,precio_dia,km_actuales,disponible,color) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, v.getMatricula());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            ps.setString(5, v.getCategoria().name());
            ps.setBigDecimal(6, v.getPrecioDia());
            ps.setInt(7, v.getKmActuales());
            ps.setBoolean(8, v.isDisponible());
            ps.setString(9, v.getColor());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar vehículo: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Vehiculo v) {
        String sql = "UPDATE vehiculos SET matricula=?,marca=?,modelo=?,anio=?,categoria=?,precio_dia=?,km_actuales=?,disponible=?,color=? WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, v.getMatricula());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            ps.setString(5, v.getCategoria().name());
            ps.setBigDecimal(6, v.getPrecioDia());
            ps.setInt(7, v.getKmActuales());
            ps.setBoolean(8, v.isDisponible());
            ps.setString(9, v.getColor());
            ps.setInt(10, v.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar vehículo: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM vehiculos WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar vehículo: " + e.getMessage());
        }
    }

    @Override
    public List<Vehiculo> listarTodos() {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos ORDER BY marca, modelo";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearVehiculo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar vehículos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Vehiculo> listarDisponibles() {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos WHERE disponible = 1 ORDER BY categoria, precio_dia";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearVehiculo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar vehículos disponibles: " + e.getMessage());
        }
        return lista;
    }

    private Vehiculo mapearVehiculo(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setId(rs.getInt("id"));
        v.setMatricula(rs.getString("matricula"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setAnio(rs.getInt("anio"));
        v.setCategoria(Vehiculo.Categoria.valueOf(rs.getString("categoria")));
        v.setPrecioDia(rs.getBigDecimal("precio_dia"));
        v.setKmActuales(rs.getInt("km_actuales"));
        v.setDisponible(rs.getBoolean("disponible"));
        v.setColor(rs.getString("color"));
        v.setImagenUrl(rs.getString("imagen_url"));
        return v;
    }
}
