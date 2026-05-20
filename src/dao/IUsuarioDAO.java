package dao;

import model.Cliente;
import model.Empleado;
import model.Usuario;
import java.util.List;

public interface IUsuarioDAO {
    Usuario validar(String username, String password);
    void registrarCliente(Cliente cliente);
    void registrarEmpleado(Empleado empleado);
    List<Usuario> listarTodos();
    void actualizar(Usuario usuario);
    void eliminar(int id);
}