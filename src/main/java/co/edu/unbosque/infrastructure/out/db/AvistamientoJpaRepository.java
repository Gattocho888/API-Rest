package co.edu.unbosque.infrastructure.out.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvistamientoJpaRepository extends JpaRepository<AvistamientoEntity, Long> {
    // Si tenías consultas personalizadas (ej. countByEspecie[cite: 5]), van aquí.
}