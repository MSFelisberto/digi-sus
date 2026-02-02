package br.com.dgs.agendamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_horarios_disponiveis")
@Getter
@Setter
public class HorarioDisponivelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long agendaId;
    private Long medicoId;
    private LocalDateTime dataHora;
    private String especialidade;
    private boolean ocupado;
    private Long consultaId;
}