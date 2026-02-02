package br.com.dgs.agendamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "tb_agendas")
@Getter
@Setter
public class AgendaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long medicoId;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private int duracaoSlotMinutos;
    private String especialidade;
    private boolean ativa;
}
