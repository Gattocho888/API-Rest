package co.edu.unbosque.application.port.in;

import co.edu.unbosque.domain.model.Avistamiento;
import java.util.List;
import java.util.Optional;

public interface AdministrarAvistamientoUseCase {
    Avistamiento guardar(Avistamiento avistamiento);
    Optional<Avistamiento> buscarPorId(Long id);
    List<Avistamiento> buscarTodos();
    Avistamiento actualizar(Long id, Avistamiento avistamiento);
    boolean eliminar(Long id);
}