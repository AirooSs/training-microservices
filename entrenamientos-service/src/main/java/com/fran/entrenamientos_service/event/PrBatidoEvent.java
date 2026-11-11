package com.fran.entrenamientos_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Hecho que ocurrio en el dominio: un usuario ha batido su record en un ejercicio
// El nombre va en pasado porque un evento describe algo que ya paso, no una orden

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrBatidoEvent {

    private Long usuarioId;
    private Long ejercicioId;
    private String nombreEjercicio;
    private Double pesoMaximo;
    private String fecha;
}
