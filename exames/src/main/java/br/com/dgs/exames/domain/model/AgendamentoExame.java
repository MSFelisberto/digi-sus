package br.com.dgs.exames.domain.model;

import br.com.dgs.exames.domain.exception.ExameBusinessException;

import java.time.LocalDateTime;
import java.util.Objects;

public class AgendamentoExame {
    private AgendamentoExameId id;
    private final SolicitacaoExameId solicitacaoExameId;
    private final TipoExameId tipoExameId;
    private final LocalDateTime dataHora;
    private StatusAgendamentoExame status;
    private final LocalDateTime dataCriacao;

    public AgendamentoExame(SolicitacaoExameId solicitacaoExameId, TipoExameId tipoExameId, LocalDateTime dataHora) {
        if (solicitacaoExameId == null)
            throw new ExameBusinessException("Solicitação de exame é obrigatória");
        if (tipoExameId == null)
            throw new ExameBusinessException("Tipo de exame é obrigatório");
        if (dataHora == null)
            throw new ExameBusinessException("Data/hora do agendamento é obrigatória");
        if (dataHora.isBefore(LocalDateTime.now()))
            throw new ExameBusinessException("Data do agendamento deve ser futura");

        this.solicitacaoExameId = solicitacaoExameId;
        this.tipoExameId = tipoExameId;
        this.dataHora = dataHora;
        this.status = StatusAgendamentoExame.AGENDADO;
        this.dataCriacao = LocalDateTime.now();
    }

    public AgendamentoExame(AgendamentoExameId id, SolicitacaoExameId solicitacaoExameId,
                            TipoExameId tipoExameId, LocalDateTime dataHora,
                            StatusAgendamentoExame status, LocalDateTime dataCriacao) {
        this.id = id;
        this.solicitacaoExameId = solicitacaoExameId;
        this.tipoExameId = tipoExameId;
        this.dataHora = dataHora;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    public void cancelar() {
        if (this.status == StatusAgendamentoExame.CANCELADO) {
            throw new ExameBusinessException("Agendamento de exame já está cancelado");
        }
        if (this.status == StatusAgendamentoExame.REALIZADO) {
            throw new ExameBusinessException("Não é possível cancelar exame já realizado");
        }
        this.status = StatusAgendamentoExame.CANCELADO;
    }

    public void realizarExame() {
        if (this.status == StatusAgendamentoExame.CANCELADO) {
            throw new ExameBusinessException("Não é possível realizar exame cancelado");
        }
        if (this.status == StatusAgendamentoExame.REALIZADO) {
            throw new ExameBusinessException("Exame já foi realizado");
        }
        this.status = StatusAgendamentoExame.REALIZADO;
    }

    public AgendamentoExameId getId() { return id; }
    public SolicitacaoExameId getSolicitacaoExameId() { return solicitacaoExameId; }
    public TipoExameId getTipoExameId() { return tipoExameId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public StatusAgendamentoExame getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public void setId(AgendamentoExameId id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendamentoExame that = (AgendamentoExame) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
