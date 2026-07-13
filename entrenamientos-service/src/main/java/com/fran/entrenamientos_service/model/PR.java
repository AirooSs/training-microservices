package com.fran.entrenamientos_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia externa a Usuario, que vive en usuarios-service (otra base de datos)
    
    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id")
    private Long usuarioId;

    @NotNull(message = "El ejercicio es obligatorio")
    @ManyToOne
    @JoinColumn(name = "ejercicio_id")
    private Ejercicio ejercicio;

    @NotNull(message = "El peso maximo es obligatorio")
    @Positive(message = "El peso maximo debe ser mayor que 0")
    private Double pesoMaximo;

    private LocalDate fecha;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDate.now();
    }
}