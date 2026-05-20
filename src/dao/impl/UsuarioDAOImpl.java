package dao.impl;

import dao.IUsuarioDAO;
import db.ConexionDB;
import model.Cliente;
import model.Empleado;
import model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements IUsuarioDAO {

    @Override
    public Usuario validar(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND activo = 1";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashGuardado = rs.getString("password");
                    if (BCrypt.checkpw(password, hashGuardado)) {
                        return mapearUsuario(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void registrarCliente(Cliente c) {
        String sqlU = "INSERT INTO usuarios (username,password,email,nombre,apellidos,dni,rol) VALUES (?,?,?,?,?,?,'cliente')";
        String sqlC = "INSERT INTO clientes (usuario_id,telefono,direccion,carnet_conducir,fecha_nacimiento) VALUES (?,?,?,?,?)";

        try (Connection con = ConexionDB.getConnection()) {
            ConexionDB.iniciarTransaccion(con);
            try (PreparedStatement psU = con.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS)) {
                psU.setString(1, c.getUsername());
                psU.setString(2, BCrypt.hashpw(c.getPassword(), BCrypt.gensalt()));
                psU.setString(3, c.getEmail());
                psU.setString(4, c.getNombre());
                psU.setString(5, c.getApellidos());
                psU.setString(6, c.getDni());
                psU.executeUpdate();

                try (ResultSet keys = psU.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        try (PreparedStatement psC = con.prepareStatement(sqlC)) {
                            psC.setInt(1, id);
                            psC.setString(2, c.getTelefono());
                            psC.setString(3, c.getDireccion());
                            psC.setString(4, c.getCarnetConducir());
                            psC.setDate(5, c.getFechaNacimiento() != null ?
                                Date.valueOf(c.getFechaNacimiento()) : null);
                            psC.executeUpdate();
                        }
                    }
                }
                ConexionDB.commit(con);
            } catch (SQLException e) {
                ConexionDB.rollback(con);
                throw new RuntimeException("Error al registrar cliente: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    @Override
    public void registrarEmpleado(Empleado e) {
        String sqlU = "INSERT INTO usuarios (username,password,email,nombre,apellidos,dni,rol) VALUES (?,?,?,?,?,?,'empleado')";
        String sqlE = "INSERT INTO empleados (usuario_id,cargo,salario,fecha_contrato) VALUES (?,?,?,?)";

        try (Connection con = ConexionDB.getConnection()) {
            ConexionDB.iniciarTransaccion(con);
            try (PreparedStatement psU = con.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS)) {
                psU.setString(1, e.getUsername());
                psU.setString(2, BCrypt.hashpw(e.getPassword(), BCrypt.gensalt()));
                psU.setString(3, e.getEmail());
                psU.setString(4, e.getNombre());
                psU.setString(5, e.getApellidos());
                psU.setString(6, e.getDni());
                psU.executeUpdate();

                try (ResultSet keys = psU.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        try (PreparedStatement psE = con.prepareStatement(sqlE)) {
                            psE.setInt(1, id);
                            psE.setString(2, e.getCargo().name());
                            psE.setBigDecimal(3, e.getSalario());
                            psE.setDate(4, e.getFechaContrato() != null ?
                                Date.valueOf(e.getFechaContrato()) : null);
                            psE.executeUpdate();
                        }
                    }
                }
                ConexionDB.commit(con);
            } catch (SQLException ex) {
                ConexionDB.rollback(con);
                throw new RuntimeException("Error al registrar empleado: " + ex.getMessage(), ex);
            }
        } catch (SQLException ex) {
            System.err.println("Error de conexión: " + ex.getMessage());
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY apellidos";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET email=?, nombre=?, apellidos=?, dni=?, activo=? WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getEmail());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getDni());
            ps.setBoolean(5, u.isActivo());
            ps.setInt(6, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setNombre(rs.getString("nombre"));
        u.setApellidos(rs.getString("apellidos"));
        u.setDni(rs.getString("dni"));
        u.setRol(Usuario.Rol.valueOf(rs.getString("rol")));
        u.setActivo(rs.getBoolean("activo"));
        Date fecha = rs.getDate("fecha_alta");
        if (fecha != null) u.setFechaAlta(fecha.toLocalDate());
        return u;
    }
}