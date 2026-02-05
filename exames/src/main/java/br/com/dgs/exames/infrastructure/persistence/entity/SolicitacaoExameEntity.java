package br.com.dgs.exames.infrastructure.persistence.entity;

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
    private Long atendimentoId;
    private Long consultaId;
    private String prioridade;
    private String observacoes;
    private String status;
    private LocalDateTime dataCriacao;
}
