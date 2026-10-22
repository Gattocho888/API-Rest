package co.edu.unbosque.validation;

import co.edu.unbosque.model.Avistamiento;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class ValidadorDeAvistamiento {

    private final Reloj reloj;

    public ValidadorDeAvistamiento(Reloj reloj) {
        this.reloj = reloj;
    }

    /**
     * Valida un avistamiento según las reglas de negocio.
     * Lanza IllegalArgumentException con el nombre del campo que falló si no es válido.
     */
    public void validar(Avistamiento avistamiento) {
        if (avistamiento == null) {
            throw new IllegalArgumentException("El cuerpo del avistamiento no puede ser nulo");
        }

        // 1. Validar especie (entre 3 y 50 caracteres)
        String especie = avistamiento.getEspecie();
        if (especie == null || especie.trim().length() < 3 || especie.trim().length() > 50) {
            throw new IllegalArgumentException("Campo inválido: 'especie' debe tener entre 3 y 50 caracteres.");
        }

        // 2. Validar fecha (formato AAAA-MM-DD y no posterior a hoy)
        String fechaStr = avistamiento.getFecha();
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo inválido: 'fecha' no puede estar vacía.");
        }

        LocalDate fechaAvistamiento;
        try {
            fechaAvistamiento = LocalDate.parse(fechaStr.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Campo inválido: 'fecha' debe tener el formato AAAA-MM-DD.");
        }

        if (fechaAvistamiento.isAfter(reloj.hoy())) {
            throw new IllegalArgumentException("Campo inválido: 'fecha' no puede ser posterior a hoy.");
        }

        // 3. Validar lugar (no vacío)
        String lugar = avistamiento.getLugar();
        if (lugar == null || lugar.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo inválido: 'lugar' no puede estar vacío.");
        }

        // 4. Validar observador (no vacío)
        String observador = avistamiento.getObservador();
        if (observador == null || observador.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo inválido: 'observador' no puede estar vacío.");
        }
    }
}