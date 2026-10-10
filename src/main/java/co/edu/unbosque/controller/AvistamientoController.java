package co.edu.unbosque.controller;

import co.edu.unbosque.model.Avistamiento;
import co.edu.unbosque.repository.AvistamientoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final AvistamientoRepository repository;


    public AvistamientoController(AvistamientoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Avistamiento>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avistamiento> ver(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Avistamiento> registrar(@RequestBody Avistamiento avistamiento) {
        if (avistamiento.getEspecie() == null || avistamiento.getLugar() == null ||
                avistamiento.getFecha() == null || avistamiento.getObservador() == null) {
            return ResponseEntity.badRequest().build();
        }
        Avistamiento nuevo = repository.save(avistamiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avistamiento> actualizar(@PathVariable Long id, @RequestBody Avistamiento datos) {
        return repository.findById(id)
                .map(avistamiento -> {
                    avistamiento.setEspecie(datos.getEspecie());
                    avistamiento.setLugar(datos.getLugar());
                    avistamiento.setFecha(datos.getFecha());
                    avistamiento.setObservador(datos.getObservador());
                    return ResponseEntity.ok(repository.save(avistamiento));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Long>> resumen() {
        List<Object[]> resultados = repository.countByEspecie();
        Map<String, Long> resumenMap = new HashMap<>();
        for (Object[] resultado : resultados) {
            resumenMap.put((String) resultado[0], (Long) resultado[1]);
        }
        return ResponseEntity.ok(resumenMap);
    }
}