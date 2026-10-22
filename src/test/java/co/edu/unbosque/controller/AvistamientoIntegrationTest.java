package co.edu.unbosque.controller;

import co.edu.unbosque.model.Avistamiento;
import co.edu.unbosque.repository.AvistamientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AvistamientoIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AvistamientoRepository repository;

    @BeforeEach
    void configurarYLimpiarBaseDeDatos() {
        // Construye MockMvc con todo el contexto web real de Spring Boot
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        // Garantiza que cada prueba de integración sea 100% independiente
        repository.deleteAll();
    }

    private Avistamiento crearAvistamiento(String especie, String fecha, String lugar, String observador) {
        Avistamiento a = new Avistamiento();
        a.setEspecie(especie);
        a.setFecha(fecha);
        a.setLugar(lugar);
        a.setObservador(observador);
        return a;
    }

    // =========================================================================
    // 1. ENDPOINT: POST /avistamientos (201 Created + Efecto GET, y 400 Bad Request)
    // =========================================================================

    @Test
    @DisplayName("POST_cuandoAvistamientoEsValido_responde201YPersisteParaPosteriorGET")
    void POST_cuandoAvistamientoEsValido_responde201YPersisteParaPosteriorGET() throws Exception {
        // Arrange
        String jsonValido = """
                {
                    "especie": "Rupicola peruvianus",
                    "fecha": "2026-10-20",
                    "lugar": "Jardín Botánico de Bogotá",
                    "observador": "Nicolás Espinosa"
                }
                """;

        // Act & Assert 1: Ejecutar POST y verificar código 201 y cuerpo completo
        mockMvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.especie", is("Rupicola peruvianus")))
                .andExpect(jsonPath("$.fecha", is("2026-10-20")))
                .andExpect(jsonPath("$.lugar", is("Jardín Botánico de Bogotá")))
                .andExpect(jsonPath("$.observador", is("Nicolás Espinosa")));

        // Obtener el ID recién creado en la BD para probar el GET posterior
        Long nuevoId = repository.findAll().get(0).getId();

        // Act & Assert 2: Verificar EFECTO haciendo GET al nuevo ID
        mockMvc.perform(get("/avistamientos/" + nuevoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(nuevoId.intValue())))
                .andExpect(jsonPath("$.especie", is("Rupicola peruvianus")))
                .andExpect(jsonPath("$.fecha", is("2026-10-20")))
                .andExpect(jsonPath("$.lugar", is("Jardín Botánico de Bogotá")))
                .andExpect(jsonPath("$.observador", is("Nicolás Espinosa")));
    }

    @Test
    @DisplayName("POST_cuandoEspecieEsInvalida_responde400ConMensajeDelCampoYNoGuarda")
    void POST_cuandoEspecieEsInvalida_responde400ConMensajeDelCampoYNoGuarda() throws Exception {
        // Arrange
        String jsonInvalido = """
                {
                    "especie": "Ab",
                    "fecha": "2026-10-20",
                    "lugar": "Jardín Botánico",
                    "observador": "Nicolás Espinosa"
                }
                """;

        // Act & Assert 1: Recibe 400 con el campo que falló
        mockMvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("especie")));

        // Act & Assert 2: Verificar efecto (no se guardó nada)
        mockMvc.perform(get("/avistamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // =========================================================================
    // 2. ENDPOINT: GET /avistamientos (200 OK con lista de datos)
    // =========================================================================

    @Test
    @DisplayName("GET_obtenerTodos_cuandoExistenRegistros_responde200ConListaCompleta")
    void GET_obtenerTodos_cuandoExistenRegistros_responde200ConListaCompleta() throws Exception {
        // Arrange
        repository.save(crearAvistamiento("Turdus fuscater", "2026-10-10", "Usaquén", "Nicolás"));
        repository.save(crearAvistamiento("Colibri coruscans", "2026-10-12", "La Calera", "Andrés"));

        // Act & Assert
        mockMvc.perform(get("/avistamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].especie", is("Turdus fuscater")))
                .andExpect(jsonPath("$[1].especie", is("Colibri coruscans")));
    }

    // =========================================================================
    // 3. ENDPOINT: GET /avistamientos/{id} (200 OK y 404 Not Found)
    // =========================================================================

    @Test
    @DisplayName("GET_obtenerPorId_cuandoExiste_responde200ConCuerpoCompleto")
    void GET_obtenerPorId_cuandoExiste_responde200ConCuerpoCompleto() throws Exception {
        // Arrange
        Avistamiento guardado = repository.save(
                crearAvistamiento("Pyrocephalus rubinus", "2026-10-18", "Humedal Córdoba", "Nicolás Espinosa")
        );

        // Act & Assert
        mockMvc.perform(get("/avistamientos/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(guardado.getId().intValue())))
                .andExpect(jsonPath("$.especie", is("Pyrocephalus rubinus")))
                .andExpect(jsonPath("$.fecha", is("2026-10-18")))
                .andExpect(jsonPath("$.lugar", is("Humedal Córdoba")))
                .andExpect(jsonPath("$.observador", is("Nicolás Espinosa")));
    }

    @Test
    @DisplayName("GET_obtenerPorId_cuandoNoExiste_responde404NotFound")
    void GET_obtenerPorId_cuandoNoExiste_responde404NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/avistamientos/9999"))
                .andExpect(status().isNotFound());
    }

    // =========================================================================
    // 4. ENDPOINT: PUT /avistamientos/{id} (200 OK + Efecto, 400 y 404)
    // =========================================================================

    @Test
    @DisplayName("PUT_actualizar_cuandoExisteYDatosSonValidos_responde200YModificaPersistencia")
    void PUT_actualizar_cuandoExisteYDatosSonValidos_responde200YModificaPersistencia() throws Exception {
        // Arrange
        Avistamiento original = repository.save(
                crearAvistamiento("Turdus fuscater", "2026-10-10", "Parque Nacional", "Nicolás")
        );

        String jsonActualizado = """
                {
                    "especie": "Turdus fuscater gigas",
                    "fecha": "2026-10-19",
                    "lugar": "Monserrate",
                    "observador": "Nicolás Espinosa"
                }
                """;

        // Act & Assert 1: Ejecutar PUT
        mockMvc.perform(put("/avistamientos/" + original.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonActualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.especie", is("Turdus fuscater gigas")))
                .andExpect(jsonPath("$.lugar", is("Monserrate")));

        // Act & Assert 2: Verificar efecto persistido con GET
        mockMvc.perform(get("/avistamientos/" + original.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.especie", is("Turdus fuscater gigas")))
                .andExpect(jsonPath("$.fecha", is("2026-10-19")))
                .andExpect(jsonPath("$.lugar", is("Monserrate")));
    }

    @Test
    @DisplayName("PUT_actualizar_cuandoLugarEstaVacio_responde400BadRequest")
    void PUT_actualizar_cuandoLugarEstaVacio_responde400BadRequest() throws Exception {
        // Arrange
        Avistamiento original = repository.save(
                crearAvistamiento("Turdus fuscater", "2026-10-10", "Parque Nacional", "Nicolás")
        );

        String jsonInvalido = """
                {
                    "especie": "Turdus fuscater",
                    "fecha": "2026-10-19",
                    "lugar": "",
                    "observador": "Nicolás Espinosa"
                }
                """;

        // Act & Assert
        mockMvc.perform(put("/avistamientos/" + original.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("lugar")));
    }

    @Test
    @DisplayName("PUT_actualizar_cuandoIdNoExiste_responde404NotFound")
    void PUT_actualizar_cuandoIdNoExiste_responde404NotFound() throws Exception {
        // Arrange
        String jsonValido = """
                {
                    "especie": "Turdus fuscater",
                    "fecha": "2026-10-19",
                    "lugar": "Monserrate",
                    "observador": "Nicolás Espinosa"
                }
                """;

        // Act & Assert
        mockMvc.perform(put("/avistamientos/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isNotFound());
    }

    // =========================================================================
    // 5. ENDPOINT: DELETE /avistamientos/{id} (204 No Content + Efecto 404, y 404)
    // =========================================================================

    @Test
    @DisplayName("DELETE_eliminar_cuandoExiste_respondeExitoYPosteriorGETDevuelve404")
    void DELETE_eliminar_cuandoExiste_respondeExitoYPosteriorGETDevuelve404() throws Exception {
        // Arrange
        Avistamiento guardado = repository.save(
                crearAvistamiento("Buteo magnirostris", "2026-10-15", "Guatavita", "Nicolás Espinosa")
        );

        // Act & Assert 1: Eliminar el registro
        mockMvc.perform(delete("/avistamientos/" + guardado.getId()))
                .andExpect(status().isNoContent());

        // Act & Assert 2: Verificar EFECTO -> GET posterior al mismo ID responde 404
        mockMvc.perform(get("/avistamientos/" + guardado.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE_eliminar_cuandoIdNoExiste_responde404NotFound")
    void DELETE_eliminar_cuandoIdNoExiste_responde404NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/avistamientos/9999"))
                .andExpect(status().isNotFound());
    }
}