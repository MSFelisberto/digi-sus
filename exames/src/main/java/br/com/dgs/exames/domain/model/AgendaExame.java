package br.com.dgs.exames.domain.model;

import br.com.dgs.exames.domain.exception.ExameBusinessException;

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
    private final TipoExameId tipoExameId;
    private final DayOfWeek diaSemana;
    private final LocalTime horaInicio;
    private final LocalTime horaFim;
    private final int duracaoSlotMinutos;
    private final int vagasPorSlot;
    private boolean ativa;

    public AgendaExame(TipoExameId tipoExameId, DayOfWeek diaSemana, LocalTime horaInicio,
                       LocalTime horaFim, int duracaoSlotMinutos, int vagasPorSlot) {
        if (tipoExameId == null) throw new ExameBusinessException("Tipo de exame é obrigatório");
        if (diaSemana == null) throw new ExameBusinessException("Dia da semana é obrigatório");
        if (horaInicio == null) throw new ExameBusinessException("Hora de início é obrigatória");
        if (horaFim == null) throw new ExameBusinessException("Hora de fim é obrigatória");
        if (!horaFim.isAfter(horaInicio))
            throw new ExameBusinessException("Hora de fim deve ser posterior à hora de início");
        if (duracaoSlotMinutos <= 0)
            throw new ExameBusinessException("Duração do slot deve ser maior que zero");
        if (vagasPorSlot <= 0)
            throw new ExameBusinessException("Vagas por slot deve ser maior que zero");
        long minutosDisponiveis = Duration.between(horaInicio, horaFim).toMinutes();
        if (duracaoSlotMinutos > minutosDisponiveis)
            throw new ExameBusinessException("Duração do slot não cabe no intervalo de horário");

        this.tipoExameId = tipoExameId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoSlotMinutos = duracaoSlotMinutos;
        this.vagasPorSlot = vagasPorSlot;
        this.ativa = true;
    }

    public AgendaExame(AgendaExameId id, TipoExameId tipoExameId, DayOfWeek diaSemana,
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

    public List<LocalDateTime> gerarSlots(LocalDate data) {
        List<LocalDateTime> slots = new ArrayList<>();
        if (data.getDayOfWeek() != this.diaSemana) {
            return slots;
        }
        LocalTime slotAtual = horaInicio;
        while (slotAtual.plusMinutes(duracaoSlotMinutos).compareTo(horaFim) <= 0) {
            slots.add(LocalDateTime.of(data, slotAtual));
            slotAtual = slotAtual.plusMinutes(duracaoSlotMinutos);
        }
        return slots;
    }

    public AgendaExameId getId() { return id; }
    public TipoExameId getTipoExameId() { return tipoExameId; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public int getDuracaoSlotMinutos() { return duracaoSlotMinutos; }
    public int getVagasPorSlot() { return vagasPorSlot; }
    public boolean isAtiva() { return ativa; }

    public void setId(AgendaExameId id) { this.id = id; }

    public void desativar() {
        this.ativa = false;
    }

    public void ativar() {
        this.ativa = true;
    }

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
