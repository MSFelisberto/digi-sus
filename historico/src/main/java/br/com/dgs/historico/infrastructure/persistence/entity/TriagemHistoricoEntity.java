package br.com.dgs.historico.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_triagens")
@Getter
@Setter
public class TriagemHistoricoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long triagemId;

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private Long funcionarioId;

    @Column(nullable = false, length = 20)
    private String pressaoArterial;

    @Column(nullable = false)
    private Double temperatura;

    @Column(nullable = false)
    private Integer batimentoCardiaco;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conduta;

    @Column(nullable = false)
    private LocalDateTime dataRegistro;
}
