package dao.impl;

import dao.IAlquilerDAO;
import db.ConexionDB;
import dto.AlquilerDTO;
import model.Alquiler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlquilerDAOImpl implements IAlquilerDAO {

    @Override
    public void insertar(Alquiler a) {
        String sql = "INSERT INTO alquileres (cliente_id,vehiculo_id,empleado_id,fecha_inicio,fecha_fin,km_salida,precio_total,estado,observaciones) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getClienteId());
            ps.setInt(2, a.getVehiculoId());
            if (a.getEmpleadoId() != null) ps.setInt(3, a.getEmpleadoId());
            else ps.setNull(3, Types.INTEGER);
            ps.setDate(4, Date.valueOf(a.getFechaInicio()));
            ps.setDate(5, Date.valueOf(a.getFechaFin()));
            ps.setInt(6, a.getKmSalida());
            ps.setBigDecimal(7, a.getPrecioTotal());
            ps.setString(8, a.getEstado().name());
            ps.setString(9, a.getObservaciones());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar alquiler: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Alquiler a) {
        String sql = "UPDATE alquileres SET fecha_fin=?,km_entrada=?,precio_total=?,estado=?,observaciones=? WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(a.getFechaFin()));
            if (a.getKmEntrada() != null) ps.setInt(2, a.getKmEntrada());
            else ps.setNull(2, Types.INTEGER);
            ps.setBigDecimal(3, a.getPrecioTotal());
            ps.setString(4, a.getEstado().name());
            ps.setString(5, a.getObservaciones());
            ps.setInt(6, a.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar alquiler: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM alquileres WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar alquiler: " + e.getMessage());
        }
    }

    @Override
    public List<AlquilerDTO> listarTodos() {
        List<AlquilerDTO> lista = new ArrayList<>();
        String sql = """
                SELECT a.id, a.fecha_inicio, a.fecha_fin, a.km_salida, a.km_entrada,
                       a.precio_total, a.estado, a.observaciones,
                       CONCAT(u.nombre,' ',u.apellidos) AS cliente_nombre,
                       u.dni AS cliente_dni,
                       v.matricula, CONCAT(v.marca,' ',v.modelo,' (',v.anio,')') AS vehiculo_nombre,
                       CONCAT(ue.nombre,' ',ue.apellidos) AS empleado_nombre,
                       DATEDIFF(a.fecha_fin, a.fecha_inicio) AS dias
                FROM alquileres a
                JOIN clientes c   ON a.cliente_id  = c.usuario_id
                JOIN usuarios u   ON c.usuario_id  = u.id
                JOIN vehiculos v  ON a.vehiculo_id = v.id
                LEFT JOIN empleados e  ON a.empleado_id = e.usuario_id
                LEFT JOIN usuarios ue  ON e.usuario_id  = ue.id
                ORDER BY a.fecha_inicio DESC
                """;
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AlquilerDTO dto = new AlquilerDTO();
                dto.setId(rs.getInt("id"));
                dto.setClienteNombreCompleto(rs.getString("cliente_nombre"));
                dto.setClienteDni(rs.getString("cliente_dni"));
                dto.setVehiculoMatricula(rs.getString("matricula"));
                dto.setVehiculoNombre(rs.getString("vehiculo_nombre"));
                dto.setEmpleadoNombre(rs.getString("empleado_nombre"));
                dto.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                dto.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                dto.setDiasAlquiler(rs.getLong("dias"));
                dto.setKmSalida(rs.getInt("