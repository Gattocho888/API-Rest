package co.edu.unbosque.application.port.in; // Asegúrate de que esté en este paquete

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
    public ResponseEntity<?> crear(@RequestBody Avistamiento avistamiento) {
        try {
            Avistamiento guardado = useCase.guardar(avistamiento);
            return new ResponseEntity<>(guardado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Retorna un JSON con el mensaje de error que espera la prueba
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
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
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Avistamiento avistamiento) {
        try {
            Avistamiento actualizado = useCase.actualizar(id, avistamiento);
            if (actualizado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
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