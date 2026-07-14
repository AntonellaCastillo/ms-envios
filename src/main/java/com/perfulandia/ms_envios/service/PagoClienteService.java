package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.dto.PagoConsultaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Cliente de comunicación con MS Pagos (comunicación REST entre microservicios).
@Service
public class PagoClienteService {

    private static final Logger log = LoggerFactory.getLogger(PagoClienteService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.pagos.url:http://localhost:8086}")
    private String urlPagos;

    // HU-48: al cancelar un pedido pagado, le pide a MS Pagos que marque el pago como RECHAZADO (reembolso).
    // Se hace en 2 pasos: 1) buscar el pago del pedido, 2) cambiar su estado a RECHAZADO.
    // try/catch = RESILIENCIA: si Pagos está caído, Envíos NO se cae.
    public void solicitarReembolso(Long idPedido) {
        try {
            // Paso 1: obtener el pago asociado al pedido
            String urlBuscar = urlPagos + "/api/v1/pagos/pedido/" + idPedido;
            PagoConsultaDTO pago = restTemplate.getForObject(urlBuscar, PagoConsultaDTO.class);

            // Paso 2: si existe, marcar su estado como RECHAZADO (reembolso)
            if (pago != null && pago.getIdPago() != null) {
                String urlEstado = urlPagos + "/api/v1/pagos/" + pago.getIdPago()
                        + "/estado?nuevoEstado=RECHAZADO";
                restTemplate.put(urlEstado, null);
                log.info("Reembolso solicitado a MS Pagos para el pedido {} (pago {})", idPedido, pago.getIdPago());
            }
        } catch (Exception e) {
            log.warn("No se pudo solicitar reembolso a MS Pagos para el pedido {}: {}", idPedido, e.getMessage());
        }
    }
}