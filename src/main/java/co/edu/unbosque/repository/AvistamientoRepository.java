package co.edu.unbosque.repository;

import co.edu.unbosque.model.Avistamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {

    @Query("SELECT a.especie, COUNT(a) FROM Avistamiento a GROUP BY a.especie")
    List<Object[]> countByEspecie();
}