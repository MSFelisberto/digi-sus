package br.com.dgs.agendamento.domain.model;

import br.com.dgs.agendamento.domain.exception.AgendaBusinessException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Agenda {
    private AgendaId id;
    private final MedicoId medicoId;
    private final DayOfWeek diaSemana;
    private final LocalTime horaInicio;
    private final LocalTime horaFim;
    private final int duracaoSlotMinutos;
    private final Especialidade especialidade;
    private boolean ativa;

    // Constutor para criar uma nova agenda
    public Agenda(MedicoId medicoId, DayOfWeek diaSemana, LocalTime horaInicio,
                  LocalTime horaFim, int duracaoSlotMinutos, Especialidade especialidade) {
        validarDadosObrigatorios(medicoId, diaSemana, horaFim, horaFim, especialidade);
        validarHorarios(horaInicio, horaFim);
        validarDuracaoSlot(duracaoSlotMinutos, horaInicio, horaFim);

        this.medicoId = medicoId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoSlotMinutos = duracaoSlotMinutos;
        this.especialidade = especialidade;
        this.ativa = true;
    }

    // Construtor de Reconstituição do Banco
    public Agenda(AgendaId id, MedicoId medicoId, DayOfWeek diaSemana,
                  LocalTime horaInicio, LocalTime horaFim,
                  int duracaoSlotMinutos, Especialidade especialidade, boolean ativa) {
        this.id = id;
        this.medicoId = medicoId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoSlotMinutos = duracaoSlotMinutos;
        this.especialidade = especialidade;
        this.ativa = ativa;
    }

    /**
     *
     * Gera os horários (slots) para uma data específica.
     * Só gera se a data cair no dia Semana desta agenda.
     */
    public List<LocalDateTime> gerarHorarios(LocalDate data) {
        List<LocalDateTime> horarios = new ArrayList<>();

        if (data.getDayOfWeek() != this.diaSemana) {
            return horarios;
        }

        LocalTime slotAtual = horaInicio;
        while (!slotAtual.isBefore(horaInicio) && slotAtual.isBefore(horaFim)) {
            LocalTime fimDoSlot = slotAtual.plusMinutes(duracaoSlotMinutos);
            // Se o fim do slot ultrapassar meia-noite (wrap-around) ou ultrapassar horaFim, para
            if (fimDoSlot.isBefore(slotAtual) || fimDoSlot.isAfter(horaFim)) {
                break;
            }
            horarios.add(LocalDateTime.of(data, slotAtual));
            slotAtual = fimDoSlot;
        }

        return horarios;
    }

    public void desativar() {
        if (!this.ativa) {
            throw new AgendaBusinessException("Agenda já está inativa");
        }
        this.ativa = false;
    }

    // Validações privadas
    private void validarDadosObrigatorios(MedicoId medicoId, DayOfWeek diaSemana,
                                          LocalTime horaInicio, LocalTime horaFim,
                                          Especialidade especialidade) {

        if (medicoId == null) throw new AgendaBusinessException("Medico é obrigatório");
        if (diaSemana == null) throw new AgendaBusinessException("Dia da semana é obrigatório");
        if (horaInicio == null) throw new AgendaBusinessException("Horario de inicio é obrigatória");
        if (horaFim == null) throw new AgendaBusinessException("Horario do fim é obrigatória");
        if (especialidade == null) throw new AgendaBusinessException("Especialidade do profissional obrigatória");

    }

    private void validarHorarios(LocalTime horaInicio, LocalTime horaFim) {
        if (!horaFim.isAfter(horaInicio)) {
            throw new AgendaBusinessException("Horario de finalização deve ser posterior ao horário de inicio");
        }
    }

    private void validarDuracaoSlot(int duracaoSlotMinutos, LocalTime horaInicio, LocalTime horaFim) {
        if (duracaoSlotMinutos <= 0) {
            throw new AgendaBusinessException("Duração do slot deve ser maior que zero");
        }

        long munotsDisponiveis = java.time.Duration.between(horaInicio, horaFim).toMinutes();
        if (duracaoSlotMinutos > munotsDisponiveis) {
            throw new AgendaBusinessException("Duração do slot não cabe no intervalo de horário");
        }
    }

    // Getters
    public AgendaId getId() {
        return id;
    }

    public MedicoId getMedicoId() {
        return medicoId;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public int getDuracaoSlotMinutos() {
        return duracaoSlotMinutos;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setId(AgendaId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agenda agenda = (Agenda) o;
        return Objects.equals(id, agenda.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
