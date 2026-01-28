package br.com.dgs.historico.domain.model;

import br.com.dgs.historico.domain.exception.HistoricoBusinessException;

import java.time.LocalDateTime;
import java.util.Objects;

public class HistoricoConsulta {
    private HistoricoId id;
    private final ConsultaId consultaId;
    private final PacienteId pacienteId;
    private MedicoId medicoId;
    private LocalDateTime dataHora;
    private  String especialidade;
    private StatusHistorico status;
    private String observacoes;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtor para nova consulta agendada
    public HistoricoConsulta(ConsultaId consultaId,
                             PacienteId pacienteId,
                             MedicoId medicoId,
                             LocalDateTime dataHora,
                             String especialidade) {
        validarDadosObrigatorios(consultaId, pacienteId, medicoId, dataHora, especialidade);

        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataHora = dataHora;
        this.especialidade = especialidade;
        this.status = StatusHistorico.AGENDADA;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor para reconstitui��ão do banco
    public HistoricoConsulta(HistoricoId id,
                             ConsultaId consultaId,
                             PacienteId pacienteId,
                             MedicoId medicoId,
                             LocalDateTime dataHora,
                             String especialidade,
                             StatusHistorico status,
                             String observacoes,
                             LocalDateTime dataCriacao,
                             LocalDateTime dataAtualizacao) {
        this.id = id;
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataHora = dataHora;
        this.especialidade = especialidade;
        this.status = status;
        this.observacoes = observacoes;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public void cancelar(String motivoCancelamento) {
        if (this.status == StatusHistorico.CANCELADA) {
            throw new HistoricoBusinessException("Consulta já está cancelada no histórico");
        }

        if (this.status == StatusHistorico.REALIZADA) {
            throw new HistoricoBusinessException("Não é possível cancelar uma consulta já realizada");
        }

        this.status = StatusHistorico.CANCELADA;
        this.observacoes = motivoCancelamento;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void reagendar(LocalDateTime novaDataHora, MedicoId novoMedico,
                          String novaEspecialidade) {
        if (this.status == StatusHistorico.CANCELADA) {
            throw new HistoricoBusinessException("Não é possível reagendar");
        }

        if (novaDataHora == null) {
            throw new HistoricoBusinessException("Nova data é obrigatória");
        }

        this.dataHora = novaDataHora;
        this.medicoId = novoMedico;
        this.especialidade = novaEspecialidade;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void marcarComoRealizada(String observacoesMedicas) {
        if (this.status == StatusHistorico.CANCELADA) {
            throw new HistoricoBusinessException("Não é possível marcar como realizada uma consulta cancelada");
        }

        if (this.status == StatusHistorico.REALIZADA) {
            throw new HistoricoBusinessException("Consulta já está marcada como realizada");
        }

        this.status = StatusHistorico.REALIZADA;
        this.observacoes = observacoesMedicas;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void adicionarObservacoes(String observacoes) {
        if (observacoes == null || observacoes.trim().isEmpty()) {
            throw new HistoricoBusinessException("Observações não podem ser vazias");
        }

        this.observacoes = observacoes;
        this.dataAtualizacao = LocalDateTime.now();
    }

    private void validarDadosObrigatorios(ConsultaId consultaId,
                                          PacienteId pacienteId,
                                          MedicoId medicoId,
                                          LocalDateTime dataHora,
                                          String especialidade) {
        if (consultaId == null) {
            throw new HistoricoBusinessException("ID da consulta é obrigatório");
        }
        if (pacienteId == null) {
            throw new HistoricoBusinessException("Paciente é obrigatório");
        }
        if (medicoId == null) {
            throw new HistoricoBusinessException("Médico é obrigatório");
        }
        if (dataHora == null) {
            throw new HistoricoBusinessException("Data e hora são obrigatórias");
        }
        if (especialidade == null || especialidade.trim().isEmpty()) {
            throw new HistoricoBusinessException("Especialidade é obrigatória");
        }
    }

    // Getters
    public HistoricoId getId() { return id; }
    public ConsultaId getConsultaId() { return consultaId; }
    public PacienteId getPacienteId() { return pacienteId; }
    public MedicoId getMedicoId() { return medicoId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getEspecialidade() { return especialidade; }
    public StatusHistorico getStatus() { return status; }
    public String getObservacoes() { return observacoes; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    void assignId(HistoricoId id) {
        if (this.id != null) {
            throw new IllegalStateException("ID já foi atribuído");
        }
        this.id = id;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricoConsulta that = (HistoricoConsulta) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
