package br.com.dgs.agendamento.domain.model;

import br.com.dgs.agendamento.domain.exception.HorarioIndisponivelException;

import java.time.LocalDateTime;
import java.util.Objects;

public class HorarioExameDisponivel {
    private HorarioExameDisponivelId id;
    private final AgendaExameId agendaExameId;
    private final Long tipoExameId;
    private final LocalDateTime dataHora;
    private final int vagasTotais;
    private int vagasOcupadas;

    public HorarioExameDisponivel(AgendaExameId agendaExameId, Long tipoExameId,
                                   LocalDateTime dataHora, int vagasTotais) {
        if (agendaExameId == null) throw new IllegalArgumentException("AgendaExameId é obrigatório");
        if (tipoExameId == null) throw new IllegalArgumentException("TipoExameId é obrigatório");
        if (dataHora == null) throw new IllegalArgumentException("Data/hora é obrigatória");
        if (vagasTotais <= 0) throw new IllegalArgumentException("Vagas totais deve ser maior que zero");

        this.agendaExameId = agendaExameId;
        this.tipoExameId = tipoExameId;
        this.dataHora = dataHora;
        this.vagasTotais = vagasTotais;
        this.vagasOcupadas = 0;
    }

    public HorarioExameDisponivel(HorarioExameDisponivelId id, AgendaExameId agendaExameId,
                                   Long tipoExameId, LocalDateTime dataHora,
                                   int vagasTotais, int vagasOcupadas) {
        this.id = id;
        this.agendaExameId = agendaExameId;
        this.tipoExameId = tipoExameId;
        this.dataHora = dataHora;
        this.vagasTotais = vagasTotais;
        this.vagasOcupadas = vagasOcupadas;
    }

    public void reservar() {
        if (vagasOcupadas >= vagasTotais) {
            throw new HorarioIndisponivelException("Não há vagas disponíveis neste horário de exame");
        }
        this.vagasOcupadas++;
    }

    public void liberar() {
        if (vagasOcupadas <= 0) {
            throw new HorarioIndisponivelException("Horário de exame não possui vagas ocupadas para liberar");
        }
        this.vagasOcupadas--;
    }

    public boolean isDisponivel() {
        return vagasOcupadas < vagasTotais;
    }

    public int getVagasRestantes() {
        return vagasTotais - vagasOcupadas;
    }

    public HorarioExameDisponivelId getId() { return id; }
    public AgendaExameId getAgendaExameId() { return agendaExameId; }
    public Long getTipoExameId() { return tipoExameId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public int getVagasTotais() { return vagasTotais; }
    public int getVagasOcupadas() { return vagasOcupadas; }

    public void setId(HorarioExameDisponivelId id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HorarioExameDisponivel that = (HorarioExameDisponivel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
