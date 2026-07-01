package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.dto.ReservaStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Cliente de comunicación con MS Productos y Stock.
// NO es una entidad.
// NO se guarda en BD.
// Sirve para llamar por HTTP a otro microservicio usando RestTemplate.
@Service
public class ProductoClienteService {

    @Autowired
    private RestTemplate restTemplate;

    // URL configurable del MS Productos y Stock.
    // Si no existe en application.properties, usa localhost:8082 por defecto.
    @Value("${ms.productos.url:http://localhost:8082}")
    private String urlProductos;

    // HU-22 / HU-23:
    // Valida que el producto exista en MS Productos y Stock.
    // Retorna true si el producto existe y el MS Productos responde correctamente.
    // Retorna false si el producto no existe o si el otro microservicio falla.
    public boolean validarProducto(Long idProducto) {
        try {
            String url = urlProductos + "/api/v1/productos/" + idProducto;

            ResponseEntity<Object> respuesta = restTemplate.getForEntity(url, Object.class);

            return respuesta.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            // Resiliencia: si MS Productos no responde, MS Envíos no se cae.
            System.out.println("No se pudo validar el producto en MS Productos: " + e.getMessage());
            return false;
        }
    }

    // HU-23:
    // Al hacer checkout, MS Envíos le pide a MS Productos y Stock reservar stock.
    // Retorna true si la reserva fue exitosa.
    // Retorna false si MS Productos falla o no responde.
    public boolean reservarStock(Long idProducto, Integer cantidad) {
        try {
            ReservaStockDTO dto = new ReservaStockDTO(idProducto, cantidad);

            String url = urlProductos + "/api/v1/inventario/reservar";

            ResponseEntity<Void> respuesta = restTemplate.postForEntity(url, dto, Void.class);

            return respuesta.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            // Resiliencia: si MS Productos está caído, MS Envíos no se cae.
            System.out.println("No se pudo reservar stock en MS Productos: " + e.getMessage());
            return false;
        }
    }

    // HU-48:
    // Si el pedido se cancela, MS Envíos puede pedir a MS Productos liberar/cancelar la reserva.
    // Retorna true si la cancelación fue exitosa.
    // Retorna false si MS Productos falla o no responde.
    public boolean cancelarReserva(Long idProducto, Integer cantidad) {
        try {
            ReservaStockDTO dto = new ReservaStockDTO(idProducto, cantidad);

            String url = urlProductos + "/api/v1/inventario/cancelar-reserva";

            ResponseEntity<Void> respuesta = restTemplate.postForEntity(url, dto, Void.class);

            return respuesta.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            // Resiliencia: si MS Productos está caído, MS Envíos no se cae.
            System.out.println("No se pudo cancelar la reserva de stock en MS Productos: " + e.getMessage());
            return false;
        }
    }
}