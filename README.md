# MS Envíos — Perfulandia SPA

Microservicio de **Pedidos Web y Logística de Envíos** del sistema Perfulandia SPA.
Gestiona el flujo de compra en línea (carrito, checkout, cupones, cancelación) y el
despacho físico de los pedidos (envíos y tracking).

## Stack
- Java 25
- Spring Boot 4.0.7
- Maven
- MySQL (XAMPP)
- Spring Data JPA · Bean Validation · Lombok · Actuator

## Configuración
- **Puerto:** 8091
- **Base de datos:** `db_perfulandia_envios` (se crea sola con `createDatabaseIfNotExist=true`)
- **Tablas:** se generan automáticamente con `ddl-auto=update` (Hibernate lee las entidades)

## Entidades
| Entidad | Rol | Descripción |
|---------|-----|-------------|
| Pedido | Cabecera | La compra web (cliente registrado o invitado) |
| DetallePedido | Detalle | Cada producto/línea del pedido |
| Envío | — | El despacho físico, con tracking y estado |
| Cupón | — | Códigos promocionales con descuento |

## Historias de Usuario cubiertas
- **HU-22** Carrito / añadir productos
- **HU-23** Checkout (confirmar pedido)
- **HU-24** Historial de pedidos por cliente
- **HU-25** Consultar estado del pedido
- **HU-27** Aplicar cupón de descuento
- **HU-33** Crear envío (genera tracking)
- **HU-34** Actualizar estado del envío
- **HU-48** Cancelar pedido (con regla de estado)
- **HU-53** Checkout de invitado (sin cuenta)
- **HU-54** Tipo de entrega (despacho o retiro)

## Endpoints principales
Base URL: `http://localhost:8091`

### Pedidos (`/api/v1/pedidos`)
- `POST` crear pedido (checkout) → 201
- `GET` listar todos
- `GET /{id}` consultar pedido
- `GET /cliente/{idCliente}` historial por cliente
- `PUT /{id}/estado?nuevoEstado=PAGADO` actualizar estado
- `PUT /{id}/cupon?codigoCupon=XXXX` aplicar cupón
- `PUT /{id}/cancelar` cancelar pedido
- `DELETE /{id}` eliminar

### Envíos (`/api/v1/envios`)
- `POST` crear envío → 201
- `GET` listar · `GET /{id}` por id
- `GET /tracking/{tracking}` rastrear
- `PUT /{id}/estado?nuevoEstado=EN_RUTA` actualizar estado
- `DELETE /{id}` eliminar

### Cupones (`/api/v1/cupones`)
- `POST` crear → 201
- `GET` listar · `GET /{id}` por id · `GET /codigo/{codigo}` por código
- `PUT /{id}` actualizar · `DELETE /{id}` eliminar

## Cómo ejecutar
1. Iniciar MySQL en XAMPP.
2. En la raíz del proyecto: `./mvnw spring-boot:run`
3. La app levanta en el puerto 8091 y crea la BD y las tablas automáticamente.

## Manejo de errores
GlobalExceptionHandler centralizado: 404 (no encontrado), 409 (operación no permitida /
integridad), 400 (validación / JSON inválido), 500 (error general).

## Autora
Antonella Castillo — Duoc UC · DSY1103 Desarrollo Fullstack I