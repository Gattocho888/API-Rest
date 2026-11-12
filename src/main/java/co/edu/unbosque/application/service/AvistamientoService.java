package co.edu.unbosque.application.service;

import co.edu.unbosque.application.port.in.AdministrarAvistamientoUseCase;
import co.edu.unbosque.application.port.out.AvistamientoRepositoryPort;
import co.edu.unbosque.domain.model.Avistamiento;
import co.edu.unbosque.domain.validation.RelojSistema;
import co.edu.unbosque.domain.validation.ValidadorDeAvistamiento;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvistamientoService implements AdministrarAvistamientoUseCase {

    private final AvistamientoRepositoryPort repositoryPort;
    private final ValidadorDeAvistamiento validador;

    // Spring inyecta automáticamente el adaptador que implementa AvistamientoRepositoryPort
    public AvistamientoService(AvistamientoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
        // Instanciamos el validador de dominio puro, pasándole el reloj que exige
        this.validador = new ValidadorDeAvistamiento(new RelojSistema());
    }

    @Override
    public Avistamiento guardar(Avistamiento avistamiento) {
        // 1. Ejecutamos la regla de negocio pura
        validador.validar(avistamiento);
        // 2. Si no lanza excepción, delegamos la persistencia al puerto de salida
        return repositoryPort.guardar(avistamiento);
    }

    @Override
    public Optional<Avistamiento> buscarPorId(Long id) {
        return repositoryPort.buscarPorId(id);
    }

    @Override
    public List<Avistamiento> buscarTodos() {
        return repositoryPort.buscarTodos();
    }

    @Override
    public Avistamiento actualizar(Long id, Avistamiento avistamiento) {
        // Verificamos si existe para evitar que JPA cree un registro nuevo por accidente
        if (!repositoryPort.existePorId(id)) {
            return null; // El controlador procesará esto y devolverá el 404 Not Found
        }

        validador.validar(avistamiento);
        avistamiento.setId(id); // Aseguramos que conserve el ID original

        return repositoryPort.guardar(avistamiento);
    }

    @Override
    public boolean eliminar(Long id) {
        if (!repositoryPort.existePorId(id)) {
            return false; // El controlador procesará esto y devolverá el 404 Not Found
        }

        repositoryPort.eliminarPorId(id);
        return true;
    }
}