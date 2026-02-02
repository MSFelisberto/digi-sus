package br.com.dgs.agendamento.infrastructure.persistence.entity.exame;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_solicitacoes_exame")
@Getter
@Setter
public class SolicitacaoExameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private Long tipoExameId;
    private String prioridade;
    private String observacoes;
    private String status;
    private LocalDateTime dataCriacao;
}