# Cuerpos JSON para probar los microservicios — Perfulandia SPA
### Antonella Castillo · Duoc UC · DSY1103
### (Verificados contra las entidades reales del código)

> **Regla de oro:** NO usar el JSON de ejemplo de Swagger (el que trae `"string"`, `0`,
> o números gigantes). Usar SIEMPRE estos cuerpos. NUNCA mandar el campo `id`
> (idCupon, idPago, etc.) -> la base de datos lo genera sola.

> **Antes de probar:** XAMPP con MySQL encendido + el MS corriendo (`./mvnw spring-boot:run`).

---

## ===========================================
## MS ENVIOS — puerto 8091
## Swagger: http://localhost:8091/swagger-ui.html
## Base de datos: db_perfulandia_envios
## ===========================================

### 1. POST Cupon -> /api/v1/cupones (201)
Obligatorios: codigo, tipoDescuento, valorDescuento
```json
{
  "codigo": "VERANO10",
  "fechaInicio": "2026-01-01T00:00:00",
  "fechaFin": "2026-12-31T23:59:59",
  "tipoDescuento": "PORCENTAJE",
  "valorDescuento": 10,
  "cantidadMaximaUsos": 100,
  "usosDisponibles": 100,
  "activo": true
}
```
tipoDescuento: PORCENTAJE o MONTO_FIJO

### 2. POST Pedido (checkout) -> /api/v1/pedidos (201)
Obligatorios: estado, tipoEntrega. Cada detalle: idProducto, cantidad, precioUnitario
```json
{
  "idCliente": 1,
  "tipoEntrega": "DESPACHO_DOMICILIO",
  "estado": "PENDIENTE_PAGO",
  "detalles": [
    { "idProducto": 1, "cantidad": 2, "precioUnitario": 15000 },
    { "idProducto": 5, "cantidad": 1, "precioUnitario": 30000 }
  ]
}
```
El total se calcula solo: (2x15000)+(1x30000) = 60000
tipoEntrega: DESPACHO_DOMICILIO o RETIRO_TIENDA
estado: PENDIENTE_PAGO, PAGADO, EN_PREPARACION, LISTO_PARA_RETIRO,
        ENVIADO, ENTREGADO, RETIRADO, CANCELADO

### 3. POST Pedido de invitado -> /api/v1/pedidos (201)
```json
{
  "nombreInvitado": "Juan Perez",
  "correoInvitado": "juan@correo.com",
  "direccionInvitado": "Av. Siempre Viva 742",
  "tipoEntrega": "DESPACHO_DOMICILIO",
  "estado": "PENDIENTE_PAGO",
  "detalles": [
    { "idProducto": 2, "cantidad": 1, "precioUnitario": 25000 }
  ]
}
```

### 4. POST Envio -> /api/v1/envios (201) — genera tracking solo
Obligatorios: idPedido, direccionDestino, estado
```json
{
  "idPedido": 1,
  "idSucursal": 1,
  "direccionDestino": "Av. Siempre Viva 742, Santiago",
  "estado": "EN_BODEGA"
}
```
estado: EN_BODEGA, EN_RUTA, ENTREGADO

### PUT / GET utiles (van en la URL, sin body):
- Aplicar cupon:  PUT /api/v1/pedidos/1/cupon?codigoCupon=VERANO10
- Cancelar:       PUT /api/v1/pedidos/1/cancelar
- Estado pedido:  PUT /api/v1/pedidos/1/estado?nuevoEstado=PAGADO
- Estado envio:   PUT /api/v1/envios/1/estado?nuevoEstado=EN_RUTA
- Historial:      GET /api/v1/pedidos/cliente/1
- Rastrear:       GET /api/v1/envios/tracking/TRK-XXXXXXXX (usar el tracking real)

---

## ===========================================
## MS PAGOS — puerto 8086
## Swagger: http://localhost:8086/swagger-ui.html
## Base de datos: db_perfulandia_pagos
## ===========================================

### 1. POST Pago valido -> /api/v1/pagos (201, queda CONFIRMADO)
Obligatorios: monto, estado, metodoPago
```json
{
  "idPedido": 1,
  "monto": 35000,
  "metodoPago": "TARJETA",
  "estado": "PENDIENTE"
}
```
estado: PENDIENTE, CONFIRMADO, RECHAZADO

### 2. POST Pago rechazado -> /api/v1/pagos (402) — KAN-23
```json
{
  "idPedido": 2,
  "monto": 0,
  "metodoPago": "TARJETA",
  "estado": "PENDIENTE"
}
```
Prueba estrella: monto 0 -> 402 Payment Required.

### 3. POST Factura -> /api/v1/facturas (201) — genera folio solo
Obligatorios: idPago, folio, montoTotal, estado
(el folio se regenera en el service, pero el campo es obligatorio en el request,
 asi que se manda un valor cualquiera y el service pone el definitivo F-XXXXXXXX)
```json
{
  "idPago": 1,
  "folio": "F-TEMP",
  "montoTotal": 35000,
  "estado": "GENERADA"
}
```
estado: GENERADA, ENVIADA, ANULADA

### PUT / GET utiles:
- Estado pago:      PUT /api/v1/pagos/1/estado?nuevoEstado=CONFIRMADO
- Estado factura:   PUT /api/v1/facturas/1/estado?nuevoEstado=ENVIADA
- Pago por pedido:  GET /api/v1/pagos/pedido/1
- Factura por pago: GET /api/v1/facturas/pago/1

---

## ===========================================
## MS SUCURSALES — puerto 8087
## Swagger: http://localhost:8087/swagger-ui.html
## Base de datos: db_perfulandia_logistica  (NO se llama "sucursales")
## ===========================================

### 1. POST Sucursal -> /api/v1/sucursales (201)
Obligatorios: nombre, direccion, latitud, longitud, horario
```json
{
  "nombre": "Sucursal Centro",
  "direccion": "Av. Libertador 1234, Santiago",
  "latitud": -33.4489,
  "longitud": -70.6693,
  "horario": "09:00-18:00"
}
```

### 2. POST Ruta -> /api/v1/rutas (201)
Obligatorios: idTransportista, fecha, zona
```json
{
  "idTransportista": 1,
  "fecha": "2026-07-01",
  "zona": "Santiago Centro"
}
```
OJO: fecha es LocalDate -> formato AAAA-MM-DD (SIN hora)

### 3. POST Destino -> /api/v1/destinos (201)
Obligatorios: idEnvio, orden, direccion, latitud, longitud, estado
```json
{
  "idEnvio": 1,
  "orden": 1,
  "direccion": "Calle Falsa 123, Santiago",
  "latitud": -33.4500,
  "longitud": -70.6700,
  "estado": "PENDIENTE"
}
```
estado: PENDIENTE o ENTREGADO
Nota: si tu POST de destino pide idRuta, agregalo (revisa en tu Swagger).

### PUT / GET utiles:
- Estado destino:   PUT /api/v1/destinos/1/estado?nuevoEstado=ENTREGADO
- Rutas x transp.:  GET /api/v1/rutas/transportista/1
- Destinos x ruta:  GET /api/v1/destinos/ruta/1

---

## RECORDATORIOS FINALES
- NUNCA mandes el campo id (lo genera la BD).
- NUNCA uses el JSON de ejemplo de Swagger ("string", 0, numeros gigantes).
- Enums SIEMPRE en MAYUSCULAS exactas (ver valores anotados en cada seccion).
- Fechas con hora (LocalDateTime): AAAA-MM-DDTHH:MM:SS  (cupon, pedido)
- Fechas sin hora (LocalDate): AAAA-MM-DD  (la fecha de la Ruta)
- Bases de datos por MS:
    Pagos      -> db_perfulandia_pagos
    Envios     -> db_perfulandia_envios
    Sucursales -> db_perfulandia_logistica  (NO "sucursales")
- Para ver los datos: entra a la base correcta en phpMyAdmin y REFRESCA la tabla.
- Orden logico de prueba:
    Envios:     Cupon -> Pedido -> Envio
    Pagos:      Pago -> Factura
    Sucursales: Sucursal -> Ruta -> Destino
