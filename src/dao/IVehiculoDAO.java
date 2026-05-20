public package dao;

import model.Vehiculo;
import java.util.List;

public interface IVehiculoDAO {
    void insertar(Vehiculo vehiculo);
    void actualizar(Vehiculo vehiculo);
    void eliminar(int id);
    List<Vehiculo> listarTodos();
    List<Vehiculo> listarDisponibles();
}
