package br.com.dgs.exames.domain.model;

import br.com.dgs.exames.domain.exception.ExameBusinessException;

import java.time.LocalDateTime;
import java.util.Objects;

public class SolicitacaoExame {
    private SolicitacaoExameId id;
    private final PacienteId pacienteId;
    private final MedicoId medicoId;
    private final TipoExameId tipoExameId;
    private final AtendimentoId atendimentoId;
    private final ConsultaId consultaId;
    private final PrioridadeExame prioridade;
    private final String observacoes;
    private StatusSolicitacaoExame status;
    private final LocalDateTime dataCriacao;

    public SolicitacaoExame(PacienteId pacienteId, MedicoId medicoId, TipoExameId tipoExameId,
                            PrioridadeExame prioridade, String observacoes) {
        this(pacienteId, medicoId, tipoExameId, null, null, prioridade, observacoes);
    }

    public SolicitacaoExame(PacienteId pacienteId, MedicoId medicoId, TipoExameId tipoExameId,
                            AtendimentoId atendimentoId, ConsultaId consultaId,
                            PrioridadeExame prioridade, String observacoes) {
        if (pacienteId == null) throw new ExameBusinessException("Paciente é obrigatório");
        if (medicoId == null) throw new ExameBusinessException("Médico é obrigatório");
        if (tipoExameId == null) throw new ExameBusinessException("Tipo de exame é obrigatório");
        if (prioridade == null) throw new ExameBusinessException("Prioridade é obrigatória");

        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.tipoExameId = tipoExameId;
        this.atendimentoId = atendimentoId;
        this.consultaId = consultaId;
        this.prioridade = prioridade;
        this.observacoes = observacoes;
        this.status = StatusSolicitacaoExame.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }

    public SolicitacaoExame(SolicitacaoExameId id, PacienteId pacienteId, MedicoId medicoId,
                            TipoExameId tipoExameId, AtendimentoId atendimentoId, ConsultaId consultaId,
                            PrioridadeExame prioridade, String observacoes,
                            StatusSolicitacaoExame status, LocalDateTime dataCriacao) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.tipoExameId = tipoExameId;
        this.atendimentoId = atendimentoId;
        this.consultaId = consultaId;
        this.prioridade = prioridade;
        this.observacoes = observacoes;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    public void agendar() {
        if (this.status != StatusSolicitacaoExame.PENDENTE) {
            throw new ExameBusinessException("Solicitação deve estar PENDENTE para ser agendada. Status atual: " + this.status);
        }
        this.status = StatusSolicitacaoExame.AGENDADA;
    }

    public void cancelar() {
        if (this.status == StatusSolicitacaoExame.CANCELADA) {
            throw new ExameBusinessException("Solicitação já está cancelada");
        }
        if (this.status == StatusSolicitacaoExame.REALIZADA) {
            throw new ExameBusinessException("Não é possível cancelar solicitação já realizada");
        }
        this.status = StatusSolicitacaoExame.CANCELADA;
    }

    public void retornarParaPendente() {
        if (this.status != StatusSolicitacaoExame.AGENDADA) {
            throw new ExameBusinessException("Só é possível retornar para PENDENTE a partir de AGENDADA");
        }
        this.status = StatusSolicitacaoExame.PENDENTE;
    }

    public SolicitacaoExameId getId() { return id; }
    public PacienteId getPacienteId() { return pacienteId; }
    public MedicoId getMedicoId() { return medicoId; }
    public TipoExameId getTipoExameId() { return tipoExameId; }
    public AtendimentoId getAtendimentoId() { return atendimentoId; }
    public ConsultaId getConsultaId() { return consultaId; }
    public PrioridadeExame getPrioridade() { return prioridade; }
    public String getObservacoes() { return observacoes; }
    public StatusSolicitacaoExame getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public void setId(SolicitacaoExameId id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolicitacaoExame that = (SolicitacaoExame) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
