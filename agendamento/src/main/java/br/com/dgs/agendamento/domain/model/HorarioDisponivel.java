package br.com.dgs.agendamento.domain.model;

import br.com.dgs.agendamento.domain.exception.HorarioIndisponivelException;

import java.time.LocalDateTime;
import java.util.Objects;

public class HorarioDisponivel {
    private HorarioDisponivelId id;
    private final AgendaId agendaId;
    private final MedicoId medicoId;
    private final LocalDateTime dataHora;
    private final Especialidade especialidade;
    private boolean ocupado;
    private ConsultaId consultaId;


    // Construtor de CRIAÇÃO (geração de slot)
    public HorarioDisponivel(AgendaId agendaId, MedicoId medicoId,
                             LocalDateTime dataHora, Especialidade especialidade) {
        if (agendaId == null) throw new IllegalArgumentException("AgendaId é obrigatório");
        if (medicoId == null) throw new IllegalArgumentException("MedicoId é obrigatório");
        if (dataHora == null) throw new IllegalArgumentException("Data/hora é obrigatória");
        if (especialidade == null) throw new IllegalArgumentException("Especialidade é obrigatória");

        this.agendaId = agendaId;
        this.medicoId = medicoId;
        this.dataHora = dataHora;
        this.especialidade = especialidade;
        this.ocupado = false;
        this.consultaId = null;
    }

    // Construtor de RECONSTITUIÇÃO (do banco)
    public HorarioDisponivel(HorarioDisponivelId id, AgendaId agendaId, MedicoId medicoId,
                             LocalDateTime dataHora, Especialidade especialidade,
                             boolean ocupado, ConsultaId consultaId) {
        this.id = id;
        this.agendaId = agendaId;
        this.medicoId = medicoId;
        this.dataHora = dataHora;
        this.especialidade = especialidade;
        this.ocupado = ocupado;
        this.consultaId = consultaId;
    }

    /**
     * Reserva este horário para uma consulta.
     * Toda a regra de negócio fica no domínio.
     */
    public void reservar(ConsultaId consultaId) {
        if (this.ocupado) {
            throw new HorarioIndisponivelException("Horário já está ocupado");
        }
        if (consultaId == null) {
            throw new IllegalArgumentException("ConsultaId é obrigatório para reservar");
        }
        this.ocupado = true;
        this.consultaId = consultaId;
    }

    /**
     * Libera este horário (ao cancelar a consulta associada).
     */
    public void liberar() {
        if (!this.ocupado) {
            throw new HorarioIndisponivelException("Horário já está livre");
        }
        this.ocupado = false;
        this.consultaId = null;
    }

    public boolean isDisponivel() {
        return !this.ocupado;
    }

    // Getters
    public HorarioDisponivelId getId() { return id; }
    public AgendaId getAgendaId() { return agendaId; }
    public MedicoId getMedicoId() { return medicoId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Especialidade getEspecialidade() { return especialidade; }
    public boolean isOcupado() { return ocupado; }
    public ConsultaId getConsultaId() { return consultaId; }

    public void setId(HorarioDisponivelId id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HorarioDisponivel that = (HorarioDisponivel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
