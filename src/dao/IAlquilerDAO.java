package dao;

import dto.AlquilerDTO;
import model.Alquiler;
import java.util.List;

public interface IAlquilerDAO {
    void insertar(Alquiler alquiler);
    void actualizar(Alquiler alquiler);
    void eliminar(int id);
    List<AlquilerDTO> listarTodos();
}
