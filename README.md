
# Microservicio de gestión de cuentas bancarias

Microservicio REST (Spring Boot 3, Java 17, Maven, H2) con arquitectura Hexagonal (ports & adapters) multi-módulo.

## Módulos
- **domain**: modelos de dominio (POJOs) y lógica básica.
- **application**: casos de uso (paquete `in`) y repository (paquete `out`).
- **infrastructure**: controladores REST (entrada `in`), adaptadores de persistencia JPA (salida `out`), configuración y arranque Spring Boot.

## Requisitos
- Java 17+
- Maven 3.9+
- Surefire 3.2+

## Ejecutar
```bash
mvn clean install
mvn -pl infrastructure spring-boot:run
```
Consola H2: `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:clientesdb`

## Endpoints
- GET `/clientes`
- GET `/clientes/mayores-de-edad`
- GET `/clientes/con-cuenta-superior-a/{cantidad}`
- GET `/clientes/{dni}`
- POST `/cuentas`
- PUT `/cuentas/{id}`

## OpenAPI (API-First)
Archivo: `infrastructure/src/main/resources/openapi/clientes-cuentas.yaml`  
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Datos iniciales
`infrastructure/src/main/resources/data.sql` se carga automáticamente.

## Tests
```bash
mvn -pl infrastructure test
```