# API REST – Registro de Avistamientos de Aves

API REST para registrar avistamientos de aves observados en el campo. Trabajo de la **Sesión 17 – Ingeniería de Software 2 (ISW-2)**.

- **Lenguaje / framework:** Java 17 + Spring Boot 4.1.1 (Spring Web MVC, Spring Data JPA)
- **Base de datos:** H2 embebida (en memoria), no requiere instalar nada
- **Formato de intercambio:** JSON (recibe y responde JSON)
- **Build:** Gradle (incluye el *wrapper*, no hace falta instalar Gradle)

---

## Requisitos

- **JDK 17 o superior** (verifica con `java -version`)
- **Git**
- **curl** para probar los endpoints (viene en Linux, macOS y Windows 10/11)

No necesitas instalar Gradle ni ninguna base de datos.

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/Gattocho888/API-Rest.git

```

### 2. Ejecutar la API

**Linux / macOS:**

```bash
./gradlew bootRun
```

**Windows (CMD o PowerShell):**

```bash
gradlew.bat bootRun
```

> Si en Linux/macOS aparece `Permission denied`, ejecuta antes `chmod +x gradlew`.

La primera vez Gradle descarga sus dependencias, así que puede tardar un poco. Cuando termine verás en la consola un mensaje como `Started ApiRestApplication`. La API queda disponible en:

```
http://localhost:8080
```

Para detenerla, presiona `Ctrl + C`.

### 3. Ejecutar las pruebas (opcional)

```bash
./gradlew test        # Linux / macOS
gradlew.bat test      # Windows
```

---

## Modelo del recurso: `avistamiento`

| Campo       | Tipo   | Descripción                                    |
|-------------|--------|------------------------------------------------|
| `id`        | number | Lo asigna el sistema automáticamente           |
| `especie`   | string | Nombre de la especie (ej. `"colibrí"`)         |
| `lugar`     | string | Dónde se observó                               |
| `fecha`     | string | Fecha en formato `AAAA-MM-DD`                  |
| `observador`| string | Quién registró el avistamiento                 |

Ejemplo:

```json
{
  "id": 1,
  "especie": "colibrí",
  "lugar": "Parque Simón Bolívar",
  "fecha": "2026-09-29",
  "observador": "Ana"
}
```

---

## Endpoints

| Operación                 | Método y ruta                 | Respuesta exitosa           | Respuesta de error                      |
|---------------------------|-------------------------------|-----------------------------|-----------------------------------------|
| Listar avistamientos      | `GET /avistamientos`          | `200 OK` + lista en JSON    | –                                       |
| Ver un avistamiento       | `GET /avistamientos/{id}`     | `200 OK` + avistamiento     | `404 Not Found` si no existe            |
| Registrar un avistamiento | `POST /avistamientos`         | `201 Created` + creado      | `400 Bad Request` si faltan datos       |
| Actualizar un avistamiento| `PUT /avistamientos/{id}`     | `200 OK` + actualizado      | `404 Not Found` si no existe            |
| Eliminar un avistamiento  | `DELETE /avistamientos/{id}`  | `204 No Content`            | `404 Not Found` si no existe            |
| Resumen por especie (bonus)| `GET /avistamientos/resumen` | `200 OK` + conteo por especie | –                                     |

---

## Ejemplos con curl

> En **Windows CMD** las comillas simples no funcionan en `-d`. Usa **PowerShell con `curl.exe`** o **Git Bash**, o escribe el JSON entre comillas dobles escapadas (`-d "{\"especie\":\"colibrí\"}"`).

### 1. Registrar un avistamiento – `POST /avistamientos`

```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"colibrí","lugar":"Parque Simón Bolívar","fecha":"2026-09-29","observador":"Ana"}'
```

Respuesta esperada: `201 Created`

```json
{
  "id": 1,
  "especie": "colibrí",
  "lugar": "Parque Simón Bolívar",
  "fecha": "2026-09-29",
  "observador": "Ana"
}
```

**Caso de error** (faltan datos, por ejemplo no se envía `lugar`, `fecha` ni `observador`):

```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"colibrí"}'
```

Respuesta esperada: `400 Bad Request`

### 2. Listar todos los avistamientos – `GET /avistamientos`

```bash
curl -i http://localhost:8080/avistamientos
```

Respuesta esperada: `200 OK`

```json
[
  {
    "id": 1,
    "especie": "colibrí",
    "lugar": "Parque Simón Bolívar",
    "fecha": "2026-09-29",
    "observador": "Ana"
  }
]
```

### 3. Ver un avistamiento – `GET /avistamientos/{id}`

```bash
curl -i http://localhost:8080/avistamientos/1
```

Respuesta esperada: `200 OK` con el avistamiento.

**Caso de error** (id inexistente):

```bash
curl -i http://localhost:8080/avistamientos/999
```

Respuesta esperada: `404 Not Found`

### 4. Actualizar un avistamiento – `PUT /avistamientos/{id}`

Se deben enviar todos los campos del avistamiento.

```bash
curl -i -X PUT http://localhost:8080/avistamientos/1 \
  -H "Content-Type: application/json" \
  -d '{"especie":"águila","lugar":"Chingaza","fecha":"2026-09-30","observador":"Ana"}'
```

Respuesta esperada: `200 OK`

```json
{
  "id": 1,
  "especie": "águila",
  "lugar": "Chingaza",
  "fecha": "2026-09-30",
  "observador": "Ana"
}
```

**Caso de error** (id inexistente):

```bash
curl -i -X PUT http://localhost:8080/avistamientos/999 \
  -H "Content-Type: application/json" \
  -d '{"especie":"águila","lugar":"Chingaza","fecha":"2026-09-30","observador":"Ana"}'
```

Respuesta esperada: `404 Not Found`

### 5. Eliminar un avistamiento – `DELETE /avistamientos/{id}`

```bash
curl -i -X DELETE http://localhost:8080/avistamientos/1
```

Respuesta esperada: `204 No Content` (sin cuerpo).

**Caso de error** (id inexistente o ya eliminado):

```bash
curl -i -X DELETE http://localhost:8080/avistamientos/999
```

Respuesta esperada: `404 Not Found`

### 6. Resumen por especie (bonus) – `GET /avistamientos/resumen`

Devuelve cuántos avistamientos hay por especie.

```bash
curl -i http://localhost:8080/avistamientos/resumen
```

Respuesta esperada: `200 OK`

```json
{
  "colibrí": 2,
  "águila": 1
}
```

---

## Prueba rápida de punta a punta

Con la API corriendo, estos comandos ejercitan todos los endpoints en orden:

```bash
curl -i -X POST http://localhost:8080/avistamientos -H "Content-Type: application/json" -d '{"especie":"colibrí","lugar":"Parque Simón Bolívar","fecha":"2026-09-29","observador":"Ana"}'
curl -i -X POST http://localhost:8080/avistamientos -H "Content-Type: application/json" -d '{"especie":"colibrí","lugar":"Jardín Botánico","fecha":"2026-09-30","observador":"Luis"}'
curl -i -X POST http://localhost:8080/avistamientos -H "Content-Type: application/json" -d '{"especie":"águila","lugar":"Chingaza","fecha":"2026-10-01","observador":"Ana"}'
curl -i http://localhost:8080/avistamientos
curl -i http://localhost:8080/avistamientos/1
curl -i http://localhost:8080/avistamientos/resumen
curl -i -X PUT http://localhost:8080/avistamientos/1 -H "Content-Type: application/json" -d '{"especie":"tucán","lugar":"Chingaza","fecha":"2026-10-02","observador":"Ana"}'
curl -i -X DELETE http://localhost:8080/avistamientos/1
curl -i http://localhost:8080/avistamientos/1
```

El último comando debe devolver `404`, porque el avistamiento ya fue eliminado.

---

## Base de datos

Los datos se guardan en **H2 en memoria** (`jdbc:h2:mem:avistamientosdb`). Esto significa que **la base de datos se crea al iniciar la API y se borra al detenerla**: al reiniciar, la lista de avistamientos empieza vacía. La tabla se genera automáticamente a partir de la entidad `Avistamiento`.

Con la API corriendo puedes inspeccionar la base de datos desde el navegador en la consola de H2:

- URL: <http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:mem:avistamientosdb`
- Usuario: `sa`
- Contraseña: `password`

---

## Estructura del proyecto

```
src/main/java/co/edu/unbosque/
├── ApiRestApplication.java                 # Punto de entrada de Spring Boot
├── model/Avistamiento.java                 # Entidad JPA (el recurso)
├── repository/AvistamientoRepository.java  # Acceso a datos (Spring Data JPA)
└── controller/AvistamientoController.java  # Endpoints REST y códigos de estado
src/main/resources/application.properties   # Configuración de H2
src/test/java/co/edu/unbosque/              # Pruebas de integración (MockMvc)
```

---

## Pruebas

El proyecto incluye pruebas de integración con **Spring Boot Test + MockMvc** (`ApiRestApplicationTests`), que verifican que la aplicación levanta y que `GET /avistamientos` responde `200`. Se ejecutan con `./gradlew test`.

---

## Uso de IA

Para este trabajo utilicé **Gemini 1.5 Pro y Claude 3.5 Sonnet para realizar la documentacion, correccion de codifo y el documento README.MD**

- Documentación oficial de Spring Boot: <https://spring.io/projects/spring-boot>
- Proyecto base generado con Spring Initializr: <https://start.spring.io>

---

**Autor:** Nicolas Espinosa Estrada – Ingeniería de Software 2, Sesión 17
