package br.com.dgs.historico.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_consultas")
@Getter
@Setter
public class HistoricoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long consultaId;

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private Long medicoId;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, length = 100)
    private String especialidade;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
}