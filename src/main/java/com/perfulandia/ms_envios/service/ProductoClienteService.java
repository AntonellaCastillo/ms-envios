package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.dto.ReservaStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Cliente de comunicación con MS Productos y Stock.
// NO es una entidad. NO se guarda en BD.
// Llama por HTTP a MS Productos usando RestTemplate.
@Service
public class ProductoClienteService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.productos.url:http://localhost:8082}")
    private String urlProductos;

    // HU-22 / HU-23: valida que el producto exista en MS Productos.
    public boolean validarProducto(Long idProducto) {
        try {
            String url = urlProductos + "/api/productos/" + idProducto;
            ResponseEntity<Object> respuesta = restTemplate.getForEntity(url, Object.class);
            return respuesta.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("No se pudo validar el producto en MS Productos: " + e.getMessage());
            return false;
        }
    }

    // HU-23: al hacer checkout, MS Envíos le pide a MS Productos apartar stock.
    // Productos expone PUT /api/inventario/apartar con AjusteStockDTO
    // (idProducto, idSucursal, cantidad, idOperacion).
    public boolean reservarStock(Long idProducto, Long idSucursal, Integer cantidad, String idOperacion) {
        try {
            ReservaStockDTO dto = new ReservaStockDTO(idProducto, idSucursal, cantidad, idOperacion);
            String url = urlProductos + "/api/inventario/apartar";
            restTemplate.put(url, dto);
            return true;
        } catch (Exception e) {
            // Resiliencia: si MS Productos está caído, MS Envíos no se cae.
            System.out.println("No se pudo apartar stock en MS Productos: " + e.getMessage());
            return false;
        }
    }

    // HU-48: si el pedido se cancela, libera el stock apartado (cantidad negativa revierte).
    public boolean cancelarReserva(Long idProducto, Long idSucursal, Integer cantidad, String idOperacion) {
        try {
            ReservaStockDTO dto = new ReservaStockDTO(idProducto, idSucursal, -cantidad, idOperacion);
            String url = urlProductos + "/api/inventario/apartar";
            restTemplate.put(url, dto);
            return true;
        } catch (Exception e) {
            System.out.println("No se pudo cancelar la reserva en MS Productos: " + e.getMessage());
            return false;
        }
    }
}