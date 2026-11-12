package co.edu.unbosque.infrastructure.in.web; // Asegúrate de que esté en este paquete

import co.edu.unbosque.application.port.in.AdministrarAvistamientoUseCase;
import co.edu.unbosque.domain.model.Avistamiento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/avistamientos") // Cambia la ruta si tenías otra
public class AvistamientoController {

    private final AdministrarAvistamientoUseCase useCase;

    // Inyectamos el puerto de entrada (Caso de uso), NO el repositorio
    public AvistamientoController(AdministrarAvistamientoUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<Avistamiento> crear(@RequestBody Avistamiento avistamiento) {
        Avistamiento guardado = useCase.guardar(avistamiento);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avistamiento> obtenerPorId(@PathVariable Long id) {
        Optional<Avistamiento> avistamiento = useCase.buscarPorId(id);
        return avistamiento.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Avistamiento>> obtenerTodos() {
        return ResponseEntity.ok(useCase.buscarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avistamiento> actualizar(@PathVariable Long id, @RequestBody Avistamiento avistamiento) {
        Avistamiento actualizado = useCase.actualizar(id, avistamiento);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (useCase.eliminar(id)) {
            // Según tu reporte de pruebas, un borrado exitoso devuelve 200 OK y luego el GET da 404
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}