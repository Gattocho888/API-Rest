package co.edu.unbosque.validation;

import co.edu.unbosque.controller.AvistamientoController;
import co.edu.unbosque.model.Avistamiento;
import co.edu.unbosque.repository.AvistamientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ValidadorDeAvistamientoTest {

    private static final LocalDate FECHA_FIJA_HOY = LocalDate.of(2026, 10, 21);
    private ValidadorDeAvistamiento validador;

    /**
     * Doble de prueba tipo STUB: Reloj Fijo que siempre devuelve 2026-10-21.
     * Evita que las pruebas de frontera temporal fallen con el paso de los días.
     */
    static class RelojFijoStub implements Reloj {
        private final LocalDate fechaFija;

        public RelojFijoStub(LocalDate fechaFija) {
            this.fechaFija = fechaFija;
        }

        @Override
        public LocalDate hoy() {
            return fechaFija;
        }
    }

    @BeforeEach
    void setUp() {
        Reloj relojStub = new RelojFijoStub(FECHA_FIJA_HOY);
        validador = new ValidadorDeAvistamiento(relojStub);
    }

    // =========================================================================
    // PRUEBAS DE ESPECIE (Partición de Equivalencia y Valores Límite: 2, 3, 50, 51)
    // =========================================================================

    @Test
    @DisplayName("validar_cuandoTodosLosCamposSonRepresentativosValidos_noLanzaExcepcion")
    void validar_cuandoTodosLosCamposSonRepresentativosValidos_noLanzaExcepcion() {
        // Arrange
        Avistamiento avistamiento = new Avistamiento(
                "Turdus fuscater", "2026-10-15", "Humedal Córdoba", "Nicolás Espinosa"
        );

        // Act & Assert
        assertDoesNotThrow(() -> validador.validar(avistamiento));
    }

    @Test
    @DisplayName("validar_cuandoEspecieTiene2Caracteres_lanzaExcepcionPorDebajoDelLimiteInferior")
    void validar_cuandoEspecieTiene2Caracteres_lanzaExcepcionPorDebajoDelLimiteInferior() {
        // Arrange (Valor límite inválido: 2 caracteres)
        Avistamiento avistamiento = new Avistamiento(
                "Ab", "2026-10-21", "Parque Simón Bolívar", "Nicolás Espinosa"
        );

        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validador.validar(avistamiento)
        );

        // Assert
        assertTrue(ex.getMessage().contains("especie"));
    }

    @Test
    @DisplayName("validar_cuandoEspecieTiene3Caracteres_esValidoEnLimiteInferiorExacto")
    void validar_cuandoEspecieTiene3Caracteres_esValidoEnLimiteInferiorExacto() {
        // Arrange (Valor límite válido: 3 caracteres)
        Avistamiento avistamiento = new Avistamiento(
                "Ave", "2026-10-21", "Parque Simón Bolívar", "Nicolás Espinosa"
        );

        // Act & Assert
        assertDoesNotThrow(() -> validador.validar(avistamiento));
    }

    @Test
    @DisplayName("validar_cuandoEspecieTiene50Caracteres_esValidoEnLimiteSuperiorExacto")
    void validar_cuandoEspecieTiene50Caracteres_esValidoEnLimiteSuperiorExacto() {
        // Arrange (Valor límite válido: exactamente 50 caracteres)
        String especie50 = "A".repeat(50);
        Avistamiento avistamiento = new Avistamiento(
                especie50, "2026-10-21", "Cerros Orientales", "Nicolás Espinosa"
        );

        // Act & Assert
        assertDoesNotThrow(() -> validador.validar(avistamiento));
    }

    @Test
    @DisplayName("validar_cuandoEspecieTiene51Caracteres_lanzaExcepcionPorEncimaDelLimiteSuperior")
    void validar_cuandoEspecieTiene51Caracteres_lanzaExcepcionPorEncimaDelLimiteSuperior() {
        // Arrange (Valor límite inválido: 51 caracteres)
        String especie51 = "A".repeat(51);
        Avistamiento avistamiento = new Avistamiento(
                especie51, "2026-10-21", "Cerros Orientales", "Nicolás Espinosa"
        );

        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validador.validar(avistamiento)
        );

        // Assert
        assertTrue(ex.getMessage().contains("especie"));
    }

    // =========================================================================
    // PRUEBAS DE FECHA (Valores Límite: Hoy vs Mañana, y Formato Inválido)
    // =========================================================================

    @Test
    @DisplayName("validar_cuandoFechaEsExactamenteHoySegunRelojStub_esValidoEnLimiteTemporal")
    void validar_cuandoFechaEsExactamenteHoySegunRelojStub_esValidoEnLimiteTemporal() {
        // Arrange (Límite temporal válido: Hoy = 2026-10-21)
        Avistamiento avistamiento = new Avistamiento(
                "Zonotrichia capensis", "2026-10-21", "Universidad El Bosque", "Nicolás Espinosa"
        );

        // Act & Assert
        assertDoesNotThrow(() -> validador.validar(avistamiento));
    }

    @Test
    @DisplayName("validar_cuandoFechaEsMananaSegunRelojStub_lanzaExcepcionPorFechaFutura")
    void validar_cuandoFechaEsMananaSegunRelojStub_lanzaExcepcionPorFechaFutura() {
        // Arrange (Límite temporal inválido: Mañana = 2026-10-22)
        Avistamiento avistamiento = new Avistamiento(
                "Zonotrichia capensis", "2026-10-22", "Universidad El Bosque", "Nicolás Espinosa"
        );

        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validador.validar(avistamiento)
        );

        // Assert
        assertTrue(ex.getMessage().contains("fecha"));
        assertTrue(ex.getMessage().contains("posterior a hoy"));
    }

    @Test
    @DisplayName("validar_cuandoFechaTieneFormatoInvalidoNoIso_lanzaExcepcionDeFormato")
    void validar_cuandoFechaTieneFormatoInvalidoNoIso_lanzaExcepcionDeFormato() {
        // Arrange (Partición inválida de formato: DD/MM/AAAA en vez de AAAA-MM-DD)
        Avistamiento avistamiento = new Avistamiento(
                "Zonotrichia capensis", "21/10/2026", "Universidad El Bosque", "Nicolás Espinosa"
        );

        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validador.validar(avistamiento)
        );

        // Assert
        assertTrue(ex.getMessage().contains("fecha"));
        assertTrue(ex.getMessage().contains("AAAA-MM-DD"));
    }

    // =========================================================================
    // PRUEBAS DE LUGAR Y OBSERVADOR (Particiones Inválidas: Vacío / Blanco)
    // =========================================================================

    @Test
    @DisplayName("validar_cuandoLugarEstaVacioOSoloEspacios_lanzaExcepcionIndicandoCampoLugar")
    void validar_cuandoLugarEstaVacioOSoloEspacios_lanzaExcepcionIndicandoCampoLugar() {
        // Arrange
        Avistamiento avistamiento = new Avistamiento(
                "Falco sparverius", "2026-10-20", "   ", "Nicolás Espinosa"
        );

        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validador.validar(avistamiento)
        );

        // Assert
        assertTrue(ex.getMessage().contains("lugar"));
    }

    @Test
    @DisplayName("validar_cuandoObservadorEstaVacio_lanzaExcepcionIndicandoCampoObservador")
    void validar_cuandoObservadorEstaVacio_lanzaExcepcionIndicandoCampoObservador() {
        // Arrange
        Avistamiento avistamiento = new Avistamiento(
                "Falco sparverius", "2026-10-20", "Humedal La Conejera", ""
        );

        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validador.validar(avistamiento)
        );

        // Assert
        assertTrue(ex.getMessage().contains("observador"));
    }

    // =========================================================================
    // PRUEBA CON DOBLE DEL REPOSITORIO (MOCKITO) - AISLAMIENTO DE PERSISTENCIA
    // =========================================================================

    @Test
    @DisplayName("crearEnControlador_cuandoAvistamientoEsInvalido_noInvocaRepositorioSave")
    void crearEnControlador_cuandoAvistamientoEsInvalido_noInvocaRepositorioSave() {
        // Arrange: Creamos un Mock del repositorio para verificar interacción sin base de datos
        AvistamientoRepository repoMock = mock(AvistamientoRepository.class);
        AvistamientoController controller = new AvistamientoController(repoMock, validador);
        Avistamiento invalido = new Avistamiento("Ab", "2026-10-21", "Bogotá", "Nicolás");

        // Act
        ResponseEntity<?> respuesta = controller.crear(invalido);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        verify(repoMock, never()).save(any(Avistamiento.class));
    }
}