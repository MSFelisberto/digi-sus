package br.com.dgs.agendamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_consultas")
@Getter
@Setter
public class ConsultaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private LocalDateTime dataHora;
    private String especialidade;
    private String status;
    private String tipoConsulta;
    private String prioridade;
    private Long triagemId;
}
