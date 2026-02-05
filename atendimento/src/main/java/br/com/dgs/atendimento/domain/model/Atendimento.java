package br.com.dgs.atendimento.domain.model;

import br.com.dgs.atendimento.domain.exception.AtendimentoBusinessException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Atendimento {
    private AtendimentoId id;
    private final ConsultaId consultaId;
    private final PacienteId pacienteId;
    private final MedicoId medicoId;
    private Anamnese anamnese;
    private CondutaMedica condutaMedica;
    private final LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusAtendimento status;

    public Atendimento(ConsultaId consultaId, PacienteId pacienteId, MedicoId medicoId) {
        validarDadosObrigatorios(consultaId, pacienteId, medicoId);

        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataHoraInicio = LocalDateTime.now();
        this.status = StatusAtendimento.EM_ANDAMENTO;
    }

    public Atendimento(AtendimentoId id, ConsultaId consultaId, PacienteId pacienteId, MedicoId medicoId,
                       Anamnese anamnese, CondutaMedica condutaMedica,
                       LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, StatusAtendimento status) {
        this.id = id;
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.anamnese = anamnese;
        this.condutaMedica = condutaMedica;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = status;
    }

    public void finalizar(Anamnese anamnese, CondutaMedica condutaMedica) {
        if (this.status == StatusAtendimento.FINALIZADO) {
            throw new AtendimentoBusinessException("Atendimento já está finalizado");
        }
        if (anamnese == null) {
            throw new AtendimentoBusinessException("Anamnese é obrigatória para finalizar o atendimento");
        }
        if (condutaMedica == null) {
            throw new AtendimentoBusinessException("Conduta médica é obrigatória para finalizar o atendimento");
        }

        this.anamnese = anamnese;
        this.condutaMedica = condutaMedica;
        this.dataHoraFim = LocalDateTime.now();
        this.status = StatusAtendimento.FINALIZADO;
    }

    public void validarPodeSolicitarExame() {
        if (this.status == StatusAtendimento.FINALIZADO) {
            throw new AtendimentoBusinessException("Não é possível solicitar exames em um atendimento finalizado");
        }
    }

    private void validarDadosObrigatorios(ConsultaId consultaId, PacienteId pacienteId, MedicoId medicoId) {
        if (consultaId == null) {
            throw new AtendimentoBusinessException("Consulta é obrigatória");
        }
        if (pacienteId == null) {
            throw new AtendimentoBusinessException("Paciente é obrigatório");
        }
        if (medicoId == null) {
            throw new AtendimentoBusinessException("Médico é obrigatório");
        }
    }

    public AtendimentoId getId() {
        return id;
    }

    public ConsultaId getConsultaId() {
        return consultaId;
    }

    public PacienteId getPacienteId() {
        return pacienteId;
    }

    public MedicoId getMedicoId() {
        return medicoId;
    }

    public Anamnese getAnamnese() {
        return anamnese;
    }

    public CondutaMedica getCondutaMedica() {
        return condutaMedica;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public StatusAtendimento getStatus() {
        return status;
    }

    public void setId(AtendimentoId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atendimento that = (Atendimento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
