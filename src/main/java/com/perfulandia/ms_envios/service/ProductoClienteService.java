package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.dto.ReservaStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Cliente de comunicación con MS Productos y Stock (comunicación REST resiliente).
@Service
public class ProductoClienteService 
{

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.productos.url:http://localhost:8082}")
    private String urlProductos;

    // Al hacer checkout, le pide a MS Productos que reserve stock de un producto.
    // try/catch = RESILIENCIA: si Productos está caído, Envíos NO se cae.
    public void reservarStock(Long idProducto, Integer cantidad) 
    {
        try 
        {
            ReservaStockDTO dto = new ReservaStockDTO(idProducto, cantidad);
            String url = urlProductos + "/api/v1/inventario/reservar";
            restTemplate.put(url, dto);
        } catch (Exception e) {
            System.out.println("No se pudo reservar stock en MS Productos: " + e.getMessage());
        }
    }
}