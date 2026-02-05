package br.com.dgs.exames.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "tb_agendas_exame")
@Getter
@Setter
public class AgendaExameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tipoExameId;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private int duracaoSlotMinutos;
    private int vagasPorSlot;
    private boolean ativa;
}
