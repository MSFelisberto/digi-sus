package br.com.dgs.atendimento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_atendimentos")
@Getter
@Setter
@NoArgsConstructor
public class AtendimentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consulta_id", nullable = false, unique = true)
    private Long consultaId;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    @Column(name = "anamnese", columnDefinition = "TEXT")
    private String anamnese;

    @Column(name = "conduta_medica", columnDefinition = "TEXT")
    private String condutaMedica;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim")
    private LocalDateTime dataHoraFim;

    @Column(name = "status", nullable = false)
    private String status;
}
