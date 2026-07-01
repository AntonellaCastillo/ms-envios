package com.perfulandia.ms_envios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Cliente de comunicación con MS Pagos (comunicación REST entre microservicios).
@Service
public class PagoClienteService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.pagos.url:http://localhost:8086}")
    private String urlPagos;

    // HU-48: al cancelar un pedido pagado, le pide a MS Pagos que haga el reembolso.
    // try/catch = RESILIENCIA: si Pagos está caído, Envíos NO se cae.
    public void solicitarReembolso(Long idPedido) {
        try {
            String url = urlPagos + "/api/v1/pagos/pedido/" + idPedido;
            restTemplate.put(url, null);
        } catch (Exception e) {
            System.out.println("No se pudo solicitar reembolso a MS Pagos: " + e.getMessage());
        }
    }
}