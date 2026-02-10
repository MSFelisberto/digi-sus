package br.com.dgs.agendamento.domain.model;

import br.com.dgs.agendamento.domain.exception.ConsultaBusinessException;

import java.time.LocalDateTime;
import java.util.Objects;

public class AgendamentoExame {
    private AgendamentoExameId id;
    private final HorarioExameDisponivelId horarioExameId;
    private final Long solicitacaoExameId;
    private final Long tipoExameId;
    private final LocalDateTime dataHora;
    private StatusAgendamentoExame status;
    private final LocalDateTime dataCriacao;

    public AgendamentoExame(HorarioExameDisponivelId horarioExameId, Long solicitacaoExameId,
                            Long tipoExameId, LocalDateTime dataHora) {
        if (horarioExameId == null)
            throw new ConsultaBusinessException("Horário de exame é obrigatório");
        if (solicitacaoExameId == null)
            throw new ConsultaBusinessException("Solicitação de exame é obrigatória");
        if (tipoExameId == null)
            throw new ConsultaBusinessException("Tipo de exame é obrigatório");
        if (dataHora == null)
            throw new ConsultaBusinessException("Data/hora do agendamento é obrigatória");
        if (dataHora.isBefore(LocalDateTime.now()))
            throw new ConsultaBusinessException("Data do agendamento deve ser futura");

        this.horarioExameId = horarioExameId;
        this.solicitacaoExameId = solicitacaoExameId;
        this.tipoExameId = tipoExameId;
        this.dataHora = dataHora;
        this.status = StatusAgendamentoExame.AGENDADO;
        this.dataCriacao = LocalDateTime.now();
    }

    public AgendamentoExame(AgendamentoExameId id, HorarioExameDisponivelId horarioExameId,
                            Long solicitacaoExameId, Long tipoExameId, LocalDateTime dataHora,
                            StatusAgendamentoExame status, LocalDateTime dataCriacao) {
        this.id = id;
        this.horarioExameId = horarioExameId;
        this.solicitacaoExameId = solicitacaoExameId;
        this.tipoExameId = tipoExameId;
        this.dataHora = dataHora;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    public void cancelar() {
        if (this.status == StatusAgendamentoExame.CANCELADO) {
            throw new ConsultaBusinessException("Agendamento de exame já está cancelado");
        }
        if (this.status == StatusAgendamentoExame.REALIZADO) {
            throw new ConsultaBusinessException("Não é possível cancelar exame já realizado");
        }
        this.status = StatusAgendamentoExame.CANCELADO;
    }

    public AgendamentoExameId getId() { return id; }
    public HorarioExameDisponivelId getHorarioExameId() { return horarioExameId; }
    public Long getSolicitacaoExameId() { return solicitacaoExameId; }
    public Long getTipoExameId() { return tipoExameId; }
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
