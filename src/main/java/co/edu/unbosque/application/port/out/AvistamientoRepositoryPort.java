package co.edu.unbosque.application.port.out;

import co.edu.unbosque.domain.model.Avistamiento;
import java.util.List;
import java.util.Optional;

public interface AvistamientoRepositoryPort {
    Avistamiento guardar(Avistamiento avistamiento);
    Optional<Avistamiento> buscarPorId(Long id);
    List<Avistamiento> buscarTodos();
    boolean existePorId(Long id);
    void eliminarPorId(Long id);
}