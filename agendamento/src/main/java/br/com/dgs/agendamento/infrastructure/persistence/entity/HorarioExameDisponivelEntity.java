package br.com.dgs.agendamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_horarios_exame_disponiveis")
@Getter
@Setter
public class HorarioExameDisponivelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long agendaExameId;
    private Long tipoExameId;
    private LocalDateTime dataHora;
    private int vagasTotais;
    private int vagasOcupadas;
}
