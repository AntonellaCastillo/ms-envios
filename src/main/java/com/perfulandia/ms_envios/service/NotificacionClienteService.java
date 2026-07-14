package com.perfulandia.ms_envios.service;

import com.perfulandia.ms_envios.dto.EstadoPedidoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Cliente de comunicación con MS Notificaciones (comunicación REST entre microservicios).
@Service
public class NotificacionClienteService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionClienteService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.notificaciones.url:http://localhost:8089}")
    private String urlNotificaciones;

    // Notifica al cliente cuando el estado de su pedido cambia.
    // try/catch = RESILIENCIA: si Notificaciones está caído, Envíos NO se cae.
    public void notificarCambioEstado(Long idPedido, String estado) {
        try {
            EstadoPedidoDTO dto = new EstadoPedidoDTO(idPedido, estado);
            String url = urlNotificaciones + "/api/notificaciones/enviar";
            restTemplate.postForObject(url, dto, Void.class);
            log.info("Notificacion de cambio de estado enviada para el pedido {}", idPedido);
        } catch (Exception e) {
            log.warn("No se pudo notificar a MS Notificaciones para el pedido {}: {}", idPedido, e.getMessage());
        }
    }
}