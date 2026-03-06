# README.md

## 1. Introducción
Este repositorio implementa el microservicio **clientes-cuentas-microservicio** con **arquitectura hexagonal multimódulo** orientada a entornos enterprise (alto volumen, baja latencia, resiliencia y mantenibilidad). El diseño separa claramente **Dominio**, **Aplicación**, **Infraestructura** y **Bootstrap** para maximizar testabilidad y reemplazo de tecnologías sin afectar al núcleo de negocio.

## 2. Objetivos de diseño
- **Escalabilidad**: consultas filtradas en BD, paginación por defecto.
- **Resiliencia**: capas desacopladas, límites de tiempo, validaciones tempranas y tests de integración.
- **Rendimiento**: filtrado directo en base de datos, búsqueda por páginas en BD.
- **Mantenibilidad**: hexágono estricto, puertos (interfaces) en el dominio, adaptadores en infraestructura, y servicios en aplicación.

## 3. Arquitectura y módulos
```
clientes-cuentas-microservicio/
├── pom.xml                      # POM padre (multimódulo)
├── domain/                      # Modelos de dominio + puertos + casos de uso (interfaces)
├── application/                 # Servicios: implementación de casos de uso (usa puertos)
├── infrastructure/              # Web (REST), persistencia JPA, Redis, Swagger, DTOs, mappers
└── bootstrap/                   # Arranque Spring Boot (App)
```

### 3.1 Flujo de dependencias (solo hacia dentro)
```
domain   <- application <- infrastructure <- bootstrap
```
- **domain**: no depende de nadie.
- **application**: depende de **domain**.
- **infrastructure**: depende de **application**.
- **bootstrap**: depende de **infrastructure**.

## 4. Dominio
- **Modelos**: `Cliente`, `CuentaBancaria` (POJOs, sin framework).
- **Puertos** (interfaces): `ClienteRepositoryPort`, `CuentaRepositoryPort`.
- **Casos de uso (interfaces)**: `BuscarClientesUseCase`, `BuscarClientePorDniUseCase`, `ClientesMayoresUseCase`, `ClientesSaldoSuperiorUseCase`.

## 5. Aplicación (servicios)
- **Implementa** los casos de uso consumiendo **puertos** de repositorio.
- **No conoce** detalles de JPA, HTTP.
- Mantiene **reglas de orquestación** (paginación, cálculo de límites de edad, etc.).

## 6. Infraestructura
### 6.1 Controladores REST / ENDPOINTS
- `GET /clientes` → paginado: `?page=0&size=20&sort=apellido1,asc`
- `GET /clientes/{dni}` → cliente con sus cuentas (consulta con `JOIN FETCH`).
- `GET /clientes/mayores-de-edad` → calculado en BD (`fecha_nacimiento <= hoy-18y`).
- `GET /clientes/con-cuenta-superior-a/{cantidad}` → `SUM(cu.total) > :cantidad`.
- `POST /cuentas` → validaciones JSR‑380 en DTO de entrada.
- `PUT /cuentas/{id}` → actualización de total con validaciones.
- `GET /cuentas/{id}` → recupera una cuenta por id (Añadido para la facilidad de las pruebas).

### 6.3 Paginación obligatoria
Todos los listados (p. ej. `/clientes`) están realizados con paginación en BD.

### 6.4 Validaciones (JSR‑380)
- `@NotBlank`, `@NotNull`, `@Positive`, `@Size`, `@Valid`, `@PositiveOrZero` en controladores y DTOs de entrada.
- Respuestas con `400 Bad Request` ante datos inválidos.
- Respuestas con `404 Not Found` ante datos no encontrados.

## 7. Bootstrap
- Este módulo actúa como punto de entrada de la aplicación, separando la lógica de negocio del arranque de Spring Boot.
- El módulo **bootstrap** es el **único ejecutable**.

## 8. Ejecución

### 8.1 Compilación
```bash
mvn clean install
```

### 8.2 Arranque del microservicio
```bash
mvn -pl bootstrap spring-boot:run
```

### 8.3 Swagger UI

- Generado automáticamente dependiendo de los controladores.
```
http://localhost:8080/swagger-ui/index.html
```

## 9. Datos de ejemplo
- Cargados desde `data.sql`

## 10. Testing

El proyecto incluye una batería completa de tests que cubren **servicios**, **controladores REST**, **validaciones**, y **persistencia JPA**.  
Se han añadido además tests específicos para la **paginación real**, tanto a nivel de dominio/aplicación como en la capa de infraestructura.

### 10.1 Unit Tests (Mockito) — Capa Application

Los tests unitarios del módulo **application** comprueban:

- Lógica de negocio pura (sin framework)
- Invocaciones correctas a los puertos (`ClienteRepositoryPort`, `CuentaRepositoryPort`)
- Manejo de excepciones como `InstanceNotFoundException`
- Métodos paginados:
    - `listar(page, size, sort)`
    - `mayoresPaged(page, size, sort)`
    - `conSaldoSuperiorPaged(cantidad, page, size, sort)`

### 10.2 Integration Tests (MockMvc) — Controladores REST

Los **MockMvc tests** (slice MVC usando `@WebMvcTest`) validan:

- El mapping de los controladores
- Serialización/deserialización JSON
- Validaciones Jakarta (`@PositiveOrZero`, `@NotBlank`, etc.)
- Paginación real en la capa web
- Manejo de errores unificado usando el `GlobalExceptionHandler` (404/400 con `ProblemDetail`)

### 10.3 Data JPA Tests (`@DataJpaTest`) — Persistencia y Adapter JPA

Los tests del módulo **infrastructure** validan la interacción real con H2:

- `ClienteRepositoryAdapterIT`
    - `findAllPaged` (consultas reales con `PageRequest`)
    - `findMayoresDeEdadPaged` (filtro en BD, paginado)
    - `findConSaldoSuperiorAPaged`
        - SUM(total) en BD
        - paginación correcta del resultado
        - carga de cuentas asociadas

Estos tests confirman que la paginación se ejecuta **en la base de datos**, no en memoria.

### 10.4 Ejecución de tests
```bash
mvn test
```

## 11. Paginación

El microservicio implementa paginación real en base de datos, desacoplada mediante arquitectura hexagonal.

### 11.1 Funcionamiento

- **Domain** define estructuras propias:
    - `PageQuery(page, size, sort)`
    - `PageResult<T>(content, page, size, totalElements, totalPages, sort)`
- **Application** trabaja solo con estas clases (no conoce Spring).
- **Infrastructure (Adapter JPA)** traduce `PageQuery` → `PageRequest` + `Sort`  
  y convierte `Page<Entity>` → `PageResult<Domain>`.

### 11.2 Características de la paginación

- Se ejecuta directamente en la BD usando `PageRequest`.
- `sort` solo acepta campos permitidos:  
  `dni`, `nombre`, `apellido1`, `apellido2`, `fechaNacimiento`.
- Si llega un sort inválido (p. ej. `string,asc` generado por Swagger):  
  → **fallback automático** a `dni,asc`.
    Controlado con la constante `ALLOWED_SORT` en `ClienteRepositoryAdapter.java`
- Endpoints paginados:
    - `GET /clientes`
    - `GET /clientes/mayores-de-edad`
    - `GET /clientes/con-cuenta-superior-a/{cantidad}`
- `con-cuenta-superior-a`:
    - Filtra en BD con `SUM(total)` + `HAVING`
    - Pagina la lista resultante de DNIs para compatibilidad con H2, 
      en otro caso (PostgreSQL/MySQL) se implementaría directamente en la query.
    - Esto se debe a un error 'No Property Full Found' al hacer la  paginación
      con GROUPBY + HAVING en H2, puede verse más información del error en este [enlace](https://www.w3tutorials.net/blog/spring-data-jpa-query-and-pageable/) 
  

### 11.3 Respuesta ejemplo (PageDto)

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 10,
  "totalElements": 42,
  "totalPages": 5,
  "sort": "dni,asc"
}
```

## 12. Ejemplos de peticiones
```bash
#Obtener un cliente por DNI
GET /clientes/111A

#Listar clientes
GET /clientes?page=0&size=5&sort=dni,asc

#Listar clientes mayores de edad
GET /clientes/mayores-de-edad?page=0&size=10&sort=apellido1,asc

#Listar clientes con saldo superior a 100000
GET /clientes/con-cuenta-superior-a?cantidad=100000&page=0&size=10

#Crear una cuenta

POST /cuentas
Content-Type: application/json
{
  "dniCliente": "999999X",
  "tipoCuenta": "NORMAL",
  "total": 1500
}

#Obtener una cuenta por ID
GET /cuentas/1

#Actualizar el total de una cuenta
PUT /cuentas/1
Content-Type: application/json

{
  "total": 950
}
