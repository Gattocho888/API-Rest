package co.edu.unbosque.controller;

import co.edu.unbosque.model.Avistamiento;
import co.edu.unbosque.repository.AvistamientoRepository;
import co.edu.unbosque.validation.ValidadorDeAvistamiento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/avistamientos") // Ajusta si tu ruta base es "/api/avistamientos"
public class AvistamientoController {

    private final AvistamientoRepository repository;
    private final ValidadorDeAvistamiento validador;

    public AvistamientoController(AvistamientoRepository repository, ValidadorDeAvistamiento validador) {
        this.repository = repository;
        this.validador = validador;
    }

    @GetMapping
    public ResponseEntity<List<Avistamiento>> obtenerTodos() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avistamiento> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Avistamiento avistamiento) {
        try {
            validador.validar(avistamiento);
            Avistamiento guardado = repository.save(avistamiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Avistamiento detalles) {
        try {
            validador.validar(detalles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

        Optional<Avistamiento> existenteOpt = repository.findById(id);
        if (existenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Avistamiento existente = existenteOpt.get();
        existente.setEspecie(detalles.getEspecie());
        existente.setFecha(detalles.getFecha());
        existente.setLugar(detalles.getLugar());
        existente.setObservador(detalles.getObservador());

        Avistamiento actualizado = repository.save(existente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build(); // O ResponseEntity.ok().build() según como lo tuvieras
    }
}