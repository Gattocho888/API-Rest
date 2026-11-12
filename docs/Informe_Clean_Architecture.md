# Informe: Taller de Clean Architecture y Arquitectura Hexagonal
**Asignatura:** Ingeniería de Software 2 (ISW-2)  
**Estudiante:** Nicolás Espinosa Estrada

---

## 1. Parte 1. Investigación

1. **Cockburn (2005) parte del problema de la filtración progresiva del código de la lógica de negocio hacia el código de la interfaz de usuario y la base de datos, lo que imposibilita realizar pruebas automatizadas de forma aislada (Cockburn, 2005).** En su propuesta, un **puerto** es una interfaz que define el propósito de una interacción de entrada o salida, aislando completamente a la aplicación del exterior (Cockburn, 2005). Un **adaptador** es el código específico de la tecnología que traduce las peticiones del mundo exterior al protocolo definido por el puerto (Cockburn, 2005). Existe una simetría entre el lado del usuario y el lado de la base de datos porque, para el núcleo del sistema, ambos son simplemente actores externos o "sistemas de entrega"; no hay distinción estructural entre enviar datos a una pantalla o a un motor de persistencia (Cockburn, 2005).

2. **Martin (2012) dibuja la arquitectura en cuatro círculos concéntricos principales:** el centro contiene las *Entities* (reglas de negocio empresariales transversales), seguido por los *Use Cases* (reglas específicas de la aplicación), luego los *Interface Adapters* (controladores, presentadores y gateways), y en el exterior los *Frameworks and Drivers* como la base de datos y la interfaz web (Martin, 2012). La **Regla de Dependencia** establece que las dependencias del código fuente solo pueden apuntar hacia adentro, hacia las políticas de mayor nivel (Martin, 2012). Esta regla prohíbe estrictamente que cualquier elemento declarado en un círculo interior conozca, mencione o importe el nombre de un elemento declarado en un círculo exterior (Martin, 2012).

3. **Clean Architecture resuelve la necesidad de guardar datos sin depender de la base de datos mediante el Principio de Inversión de Dependencias (Martin, 2012).** En lugar de que el caso de uso llame directamente a la implementación de la base de datos, el círculo interno del *Use Case* declara una interfaz abstracta (`AvistamientoRepositoryPort`) (Martin, 2012). El círculo exterior de *Interface Adapters* provee una clase (`AvistamientoDbAdapter`) que implementa esa interfaz y maneja la persistencia real (Martin, 2012). En tiempo de ejecución, el caso de uso invoca su propia interfaz interna, pero el adaptador externo ejecuta la acción, logrando que el código fuente apunte hacia adentro mientras el flujo de control cruza la frontera hacia afuera (Martin, 2012).

4. **La arquitectura hexagonal (Cockburn, 2005), la de cebolla (Palermo, 2008) y Clean Architecture (Martin, 2012) tienen en común** el objetivo fundamental de aislar el modelo y las reglas de negocio en el centro del sistema, independizándolas por completo de la interfaz de usuario, de los frameworks y de las bases de datos mediante la inversión de dependencias. Lo que cambia de una a otra es el vocabulario, la granularidad y la formalidad geométrica de las capas. Cockburn (2005) establece solo dos grandes áreas (adentro y afuera) mediadas por puertos y adaptadores; Palermo (2008) introduce anillos concéntricos formales para estructurar los servicios del núcleo; y Martin (2012) estandariza estos conceptos en un modelo universal rigurosamente articulado por la Regla de Dependencia.

5. **Un costo (trade-off) de aplicar Clean Architecture a la API de avistamientos** es la sobreingeniería inicial: se multiplica la cantidad de interfaces, adaptadores y clases mapeadoras requeridas para ejecutar operaciones CRUD simples, lo que incrementa el tiempo de desarrollo (Martin, 2012). Un **beneficio concreto** es la testabilidad absoluta, ya que permite ejecutar las pruebas unitarias y de integración de la lógica de negocio de forma aislada, sin levantar el contexto del framework web ni requerir una conexión real a la base de datos (Cockburn, 2005). **Posición:** Aunque en un entorno puramente transaccional un prototipo pequeño no requeriría tantas capas, en el contexto de una entrega de evaluación académica rigurosa para la asignatura de Ingeniería de Software 2 el beneficio de separar limpiamente las reglas de negocio justifica por completo el esfuerzo, garantizando que el diseño cumpla con los estándares arquitectónicos evaluados.

### Referencias
* Cockburn, A. (2005). Hexagonal architecture. https://alistair.cockburn.us/hexagonal-architecture/
* Martin, R. C. (2012). The Clean Architecture. https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
* Palermo, J. (2008). The Onion Architecture: part 1. https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/

---

## 2. Repositorio del Proyecto
* **Enlace al repositorio:** `[Inserta aquí el enlace de tu repositorio de GitHub/GitLab]`
* **Rama utilizada:** `refactor-hexagonal`

---

## 3. Arquitectura Elegida y Organización del Código

* **Arquitectura seleccionada:** Arquitectura Hexagonal (Puertos y Adaptadores).

### Estructura de Carpetas
```text
co.edu.unbosque
├── domain/
│   ├── model/
│   │   └── Avistamiento.java
│   └── validation/
│       └── ValidadorDeAvistamiento.java
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   └── AdministrarAvistamientoUseCase.java
│   │   └── out/
│   │       └── AvistamientoRepositoryPort.java
│   └── service/
│       └── AvistamientoService.java
└── infrastructure/
    ├── in/
    │   └── web/
    │       └── AvistamientoController.java
    └── out/
        └── db/
            ├── AvistamientoEntity.java
            ├── AvistamientoJpaRepository.java
            └── AvistamientoDbAdapter.java


Pruebas después del refactor: Tras ajustar los adaptadores, las pruebas de integración volvieron a pasar satisfactoriamente en verde (BUILD SUCCESSFUL), manteniendo exactamente las mismas respuestas HTTP (201 Created, 200 OK, 400 Bad Request, 404 Not Found).
Ubicación del reporte local: El informe detallado HTML generado por JaCoCo se encuentra disponible en API Rest/build/reports/jacoco/test/html/index.html.

5. Dificultades en la Separación
¿Qué fue lo más difícil de separar y por qué?
El mayor desafío de separación en este refactor fue aislar la persistencia de datos (Spring Data JPA) del modelo de negocio. En la versión inicial de la API organizada alrededor del framework, la clase Avistamiento funcionaba simultáneamente como regla de dominio y como tabla de base de datos mediante la anotación @Entity.

Para cumplir rigurosamente con la arquitectura hexagonal, fue obligatorio desacoplar este comportamiento creando dos representaciones independientes: un POJO puro en el dominio (Avistamiento) y una clase dedicada a la persistencia en la infraestructura (AvistamientoEntity). Esto exigió implementar adaptadores traductores (Mappers) para convertir los objetos de dominio a entidades JPA y viceversa en cada operación de base de datos. Aunque incrementa la cantidad de código repetitivo para un CRUD sencillo, es el precio necesario para garantizar que el núcleo del negocio permanezca completamente libre de dependencias tecnológicas externas.