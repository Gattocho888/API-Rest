package co.edu.unbosque.validation;

import co.edu.unbosque.domain.model.Avistamiento;
import co.edu.unbosque.domain.validation.ValidadorDeAvistamiento;
import co.edu.unbosque.domain.validation.Reloj;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorDeAvistamientoTest {

    private ValidadorDeAvistamiento validador;

    @BeforeEach
    void setUp() {
        // Reloj de prueba fijo o simulado si lo requiere tu lógica anterior
        Reloj relojFijo = () -> LocalDate.parse("2026-10-20");
        validador = new ValidadorDeAvistamiento(relojFijo);
    }

    @Test
    void cuandoAvistamientoEsValido_noLanzaExcepcion() {
        Avistamiento a = new Avistamiento();
        a.setEspecie("Rupicola peruvianus");
        a.setLugar("Jardín Botánico");
        a.setFecha("2026-10-20");
        a.setObservador("Nicolás");

        assertDoesNotThrow(() -> validador.validar(a));
    }

    @Test
    void cuandoEspecieEsInvalida_lanzaExcepcion() {
        Avistamiento a = new Avistamiento();
        a.setEspecie("Ab"); // Demasiado corta
        a.setLugar("Jardín Botánico");
        a.setFecha("2026-10-20");
        a.setObservador("Nicolás");

        assertThrows(IllegalArgumentException.class, () -> validador.validar(a));
    }
}