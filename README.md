# 🦅 API REST de Avistamientos de Aves

**Asignatura:** Ingeniería de Software 2 (ISW-2) – Universidad El Bosque  
**Autor:** Nicolás Espinosa  
**Tecnologías:** Java 17, Spring Boot, Spring Data JPA, H2 Database, Gradle, JUnit 5, Mockito, MockMvc y JaCoCo.

---

## 📋 Descripción del Proyecto

Esta API REST permite registrar, consultar, actualizar y eliminar avistamientos de aves. Como parte del taller de la **Sesión 22 (Pruebas Unitarias y de Integración)**, la arquitectura fue refactorizada para desacoplar la lógica de validación y el reloj del sistema, permitiendo una suite completa de pruebas automatizadas en distintos niveles de la pirámide.

### Regla de Negocio Implementada (`ValidadorDeAvistamiento`)
Un avistamiento es válido únicamente si cumple todas las siguientes condiciones:
1. **`especie`**: Cadena de texto que tiene entre **3 y 50 caracteres** (después de `trim()`).
2. **`fecha`**: Tiene el formato ISO **`AAAA-MM-DD`** y **no es posterior a la fecha de hoy** (evaluada a través de la interfaz `Reloj`).
3. **`lugar`**: No puede ser nulo ni estar vacío.
4. **`observador`**: No puede ser nulo ni estar vacío.

Si alguno de los campos incumple la regla en las operaciones `POST` o `PUT`, la API responde con estado **`400 Bad Request`** y un cuerpo JSON indicando explícitamente qué campo falló:
```json
{
  "error": "Campo inválido: 'especie' debe tener entre 3 y 50 caracteres."
}
```

---

## 🚀 Cómo ejecutar la API localmente

###En Windows (PowerShell / CMD):
```powershell
.\gradlew.bat bootRun
```

### En Linux / macOS:
```bash
./gradlew bootRun
```

La API quedará disponible en: `http://localhost:8080/avistamientos`

---

## 🌐 Endpoints Disponibles

| Método HTTP | Ruta | Descripción | Códigos de Respuesta |
|---|---|---|---|
| `GET` | `/avistamientos` | Obtiene la lista completa de avistamientos registrados. | `200 OK` |
| `GET` | `/avistamientos/{id}` | Obtiene un avistamiento específico por su ID. | `200 OK`, `404 Not Found` |
| `POST` | `/avistamientos` | Crea un nuevo avistamiento tras validar la regla de negocio. | `201 Created`, `400 Bad Request` |
| `PUT` | `/avistamientos/{id}` | Actualiza un avistamiento existente tras validar los datos. | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/avistamientos/{id}` | Elimina un avistamiento existente por su ID. | `204 No Content`, `404 Not Found` |

### Ejemplo de cuerpo JSON válido (`POST` / `PUT`):
```json
{
  "especie": "Rupicola peruvianus",
  "fecha": "2026-10-20",
  "lugar": "Jardín Botánico de Bogotá",
  "observador": "Nicolás Espinosa"
}
```

---

## 🧪 Ejecución de Pruebas y Cobertura de Código (JaCoCo)

El proyecto cuenta con dos niveles de pruebas ubicados en `src/test/java/co/edu/unbosque/`:
* **Pruebas Unitarias (`validation/ValidadorDeAvistamientoTest.java`):** 11 pruebas aisladas aplicando **Partición de Equivalencia**, **Valores Límite** (2, 3, 50 y 51 caracteres; fecha de hoy y de mañana) y **Dobles de Prueba** (`RelojFijoStub` y `Mock` de `AvistamientoRepository` con Mockito).
* **Pruebas de Integración (`controller/AvistamientoIntegrationTest.java`):** 9 pruebas a través de HTTP (`@SpringBootTest` + `MockMvc`) que verifican los 5 endpoints, los códigos de estado (`200`, `201`, `204`, `400`, `404`), el cuerpo JSON devuelto y los efectos reales en la base de datos H2 con limpieza previa en cada test (`@BeforeEach`).

### 1. Comando para correr todas las pruebas (Unitarias + Integración)

En **Windows (PowerShell / CMD)**:
```powershell
.\gradlew.bat test
```

En **Linux / macOS**:
```bash
./gradlew test
```

### 2. Comando para generar el reporte de cobertura con JaCoCo

En **Windows (PowerShell / CMD)**:
```powershell
.\gradlew.bat test jacocoTestReport
```

En **Linux / macOS**:
```bash
./gradlew test jacocoTestReport
```

Una vez ejecutado el comando, el reporte HTML interactivo de JaCoCo se genera en la siguiente ruta del proyecto:
```text
build/reports/jacoco/index.html
```

---

## 📄 Documento de Justificación del Taller

El análisis detallado de las decisiones de diseño, la tabla de casos de prueba (Partición de Equivalencia y Valores Límite), la justificación de niveles, el uso de dobles (`Stub` y `Mock`), las pruebas descartadas y la lectura crítica del reporte de cobertura se encuentran en:

👉 **[`docs/justificacion-pruebas.md`](docs/justificacion-pruebas.md)**