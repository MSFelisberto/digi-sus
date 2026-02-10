package br.com.dgs.agendamento.domain.model;

import br.com.dgs.agendamento.domain.exception.AgendaBusinessException;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AgendaExame {
    private AgendaExameId id;
    private final Long tipoExameId;
    private final DayOfWeek diaSemana;
    private final LocalTime horaInicio;
    private final LocalTime horaFim;
    private final int duracaoSlotMinutos;
    private final int vagasPorSlot;
    private boolean ativa;

    public AgendaExame(Long tipoExameId, DayOfWeek diaSemana, LocalTime horaInicio,
                       LocalTime horaFim, int duracaoSlotMinutos, int vagasPorSlot) {
        if (tipoExameId == null) throw new AgendaBusinessException("Tipo de exame é obrigatório");
        if (diaSemana == null) throw new AgendaBusinessException("Dia da semana é obrigatório");
        if (horaInicio == null) throw new AgendaBusinessException("Hora de início é obrigatória");
        if (horaFim == null) throw new AgendaBusinessException("Hora de fim é obrigatória");
        if (!horaFim.isAfter(horaInicio))
            throw new AgendaBusinessException("Hora de fim deve ser posterior à hora de início");
        if (duracaoSlotMinutos <= 0)
            throw new AgendaBusinessException("Duração do slot deve ser maior que zero");
        if (vagasPorSlot <= 0)
            throw new AgendaBusinessException("Vagas por slot deve ser maior que zero");
        long minutosDisponiveis = Duration.between(horaInicio, horaFim).toMinutes();
        if (duracaoSlotMinutos > minutosDisponiveis)
            throw new AgendaBusinessException("Duração do slot não cabe no intervalo de horário");

        this.tipoExameId = tipoExameId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoSlotMinutos = duracaoSlotMinutos;
        this.vagasPorSlot = vagasPorSlot;
        this.ativa = true;
    }

    public AgendaExame(AgendaExameId id, Long tipoExameId, DayOfWeek diaSemana,
                       LocalTime horaInicio, LocalTime horaFim, int duracaoSlotMinutos,
                       int vagasPorSlot, boolean ativa) {
        this.id = id;
        this.tipoExameId = tipoExameId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoSlotMinutos = duracaoSlotMinutos;
        this.vagasPorSlot = vagasPorSlot;
        this.ativa = ativa;
    }

    public List<LocalDateTime> gerarHorarios(LocalDate data) {
        List<LocalDateTime> horarios = new ArrayList<>();
        if (data.getDayOfWeek() != this.diaSemana) {
            return horarios;
        }
        LocalTime slotAtual = horaInicio;
        while (slotAtual.plusMinutes(duracaoSlotMinutos).compareTo(horaFim) <= 0) {
            horarios.add(LocalDateTime.of(data, slotAtual));
            slotAtual = slotAtual.plusMinutes(duracaoSlotMinutos);
        }
        return horarios;
    }

    public void desativar() {
        if (!this.ativa) {
            throw new AgendaBusinessException("Agenda de exame já está inativa");
        }
        this.ativa = false;
    }

    public AgendaExameId getId() { return id; }
    public Long getTipoExameId() { return tipoExameId; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public int getDuracaoSlotMinutos() { return duracaoSlotMinutos; }
    public int getVagasPorSlot() { return vagasPorSlot; }
    public boolean isAtiva() { return ativa; }

    public void setId(AgendaExameId id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendaExame that = (AgendaExame) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
