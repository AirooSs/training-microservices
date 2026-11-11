package com.fran.entrenamientos_service.service;

import com.fran.entrenamientos_service.event.PrBatidoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PrEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PrEventPublisher.class);
    public static final String TOPIC = "pr-batido";

    private final KafkaTemplate<String, PrBatidoEvent> kafkaTemplate;

    public PrEventPublisher(KafkaTemplate<String, PrBatidoEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Publica el evento sin hacer fallar el registro del PR si Kafka no esta disponible
    // El PR ya esta guardado en base de datos, la notificacion es secundaria
    public void publicar(PrBatidoEvent evento) {
        try {
            // La clave es el usuarioId: los eventos del mismo usuario van a la misma particion
            // y se consumen en orden
            kafkaTemplate.send(TOPIC, String.valueOf(evento.getUsuarioId()), evento);
        } catch (Exception ex) {
            log.warn("No se pudo publicar el evento pr-batido: {}", ex.getMessage());
        }
    }
}
