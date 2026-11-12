package co.edu.unbosque.infrastructure.out.db;

import co.edu.unbosque.application.port.out.AvistamientoRepositoryPort;
import co.edu.unbosque.domain.model.Avistamiento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AvistamientoDbAdapter implements AvistamientoRepositoryPort {

    private final AvistamientoJpaRepository jpaRepository;

    public AvistamientoDbAdapter(AvistamientoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Avistamiento guardar(Avistamiento avistamiento) {
        // 1. Traduce de Dominio a Infraestructura (Entity)
        AvistamientoEntity entity = toEntity(avistamiento);

        // 2. Guarda en la base de datos usando Spring Data
        AvistamientoEntity entityGuardada = jpaRepository.save(entity);

        // 3. Traduce de vuelta a Dominio y retorna
        return toDomain(entityGuardada);
    }

    @Override
    public Optional<Avistamiento> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain); // Si lo encuentra, lo traduce a Dominio
    }

    @Override
    public List<Avistamiento> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain) // Traduce la lista entera a Dominio
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void eliminarPorId(Long id) {
        jpaRepository.deleteById(id);
    }

    // --- MÉTODOS TRADUCTORES (MAPPERS) MANUALES ---

    private Avistamiento toDomain(AvistamientoEntity entity) {
        Avistamiento dominio = new Avistamiento();
        dominio.setId(entity.getId());
        dominio.setEspecie(entity.getEspecie());
        dominio.setLugar(entity.getLugar());
        // Setear el resto de tus atributos aquí...
        return dominio;
    }

    private AvistamientoEntity toEntity(Avistamiento dominio) {
        AvistamientoEntity entity = new AvistamientoEntity();
        entity.setId(dominio.getId());
        entity.setEspecie(dominio.getEspecie());
        entity.setLugar(dominio.getLugar());
        // Setear el resto de tus atributos aquí...
        return entity;
    }
}