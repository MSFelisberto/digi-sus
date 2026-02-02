package br.com.dgs.agendamento.infrastructure.persistence.entity.exame;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_agendamentos_exame")
@Getter
@Setter
public class AgendamentoExameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long solicitacaoExameId;
    private Long tipoExameId;
    private LocalDateTime dataHora;
    private String status;
    private LocalDateTime dataCriacao;
}