package br.com.dgs.agendamento.domain.model;

import br.com.dgs.agendamento.domain.exception.ConsultaBusinessException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Consulta {
    private ConsultaId id;
    private PacienteId pacienteId;
    private MedicoId medicoId;
    private LocalDateTime dataHora;
    private Especialidade especialidade;
    private StatusConsulta status;
    private TipoConsulta tipoConsulta;
    private Prioridade prioridade;
    private Long triagemId;

    public Consulta(PacienteId pacienteId,
                    MedicoId medicoId,
                    LocalDateTime dataHora,
                    Especialidade especialidade)
    {
        validarDadosObrigatorios(pacienteId, medicoId, dataHora, especialidade);
        validarDataFutura(dataHora);

        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataHora = dataHora;
        this.especialidade = especialidade;
        this.status = StatusConsulta.AGENDADA;
        this.tipoConsulta = TipoConsulta.REGULAR;
        this.prioridade = null;
        this.triagemId = null;
    }

    public Consulta(ConsultaId id,
                    PacienteId pacienteId,
                    MedicoId medicoId,
                    LocalDateTime dataHora,
                    Especialidade especialidade,
                    StatusConsulta status,
                    TipoConsulta tipoConsulta,
                    Prioridade prioridade,
                    Long triagemId)
    {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataHora = dataHora;
        this.especialidade = especialidade;
        this.status = status;
        this.tipoConsulta = tipoConsulta != null ? tipoConsulta : TipoConsulta.REGULAR;
        this.prioridade = prioridade;
        this.triagemId = triagemId;
    }

    public static Consulta criarConsultaTriagem(PacienteId pacienteId,
                                                 MedicoId medicoId,
                                                 LocalDateTime dataHora,
                                                 Especialidade especialidade,
                                                 TipoConsulta tipoConsulta,
                                                 Prioridade prioridade,
                                                 Long triagemId)
    {
        validarDadosObrigatoriosStatic(pacienteId, medicoId, dataHora, especialidade);

        Consulta consulta = new Consulta();
        consulta.pacienteId = pacienteId;
        consulta.medicoId = medicoId;
        consulta.dataHora = dataHora;
        consulta.especialidade = especialidade;
        consulta.status = StatusConsulta.AGENDADA;
        consulta.tipoConsulta = tipoConsulta;
        consulta.prioridade = prioridade;
        consulta.triagemId = triagemId;
        return consulta;
    }

    private Consulta() {}

    public void reagendar(LocalDateTime novaDataHora,
                          MedicoId novoMedico,
                          Especialidade novaEspecialidade)
    {
        if (status == StatusConsulta.CANCELADA) {
            throw new ConsultaBusinessException("Não é possível reagendar uma consulta cancelada");
        }
        if (status == StatusConsulta.EM_ATENDIMENTO) {
            throw new ConsultaBusinessException("Não é possível reagendar uma consulta em atendimento");
        }
        if (status == StatusConsulta.REALIZADA) {
            throw new ConsultaBusinessException("Não é possível reagendar uma consulta já realizada");
        }

        validarDataFutura(novaDataHora);

        this.dataHora = novaDataHora;
        this.medicoId = novoMedico;
        this.especialidade = novaEspecialidade;
    }

    public void cancelar() {
        if (status == StatusConsulta.CANCELADA) {
            throw new ConsultaBusinessException("Consulta já está cancelada");
        }
        if (status == StatusConsulta.EM_ATENDIMENTO) {
            throw new ConsultaBusinessException("Não é possível cancelar uma consulta em atendimento");
        }

        if (tipoConsulta != TipoConsulta.ENCAIXE && dataHora.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new ConsultaBusinessException("Não é possível cancelar consulta com menos de 24h de antecedência");
        }

        this.status = StatusConsulta.CANCELADA;
    }

    public void iniciarAtendimento() {
        if (status == StatusConsulta.CANCELADA) {
            throw new ConsultaBusinessException("Não é possível iniciar atendimento de uma consulta cancelada");
        }
        if (status == StatusConsulta.EM_ATENDIMENTO) {
            throw new ConsultaBusinessException("Consulta já está em atendimento");
        }
        if (status == StatusConsulta.REALIZADA) {
            throw new ConsultaBusinessException("Consulta já foi realizada");
        }
        this.status = StatusConsulta.EM_ATENDIMENTO;
    }

    public void marcarComoRealizada() {
        if (status == StatusConsulta.CANCELADA) {
            throw new ConsultaBusinessException("Não é possível marcar como realizada uma consulta cancelada");
        }
        if (status == StatusConsulta.REALIZADA) {
            throw new ConsultaBusinessException("Consulta já está marcada como realizada");
        }
        this.status = StatusConsulta.REALIZADA;
    }


    private void validarDadosObrigatorios(PacienteId pacienteId,
                                          MedicoId medicoId,
                                          LocalDateTime dataHora,
                                          Especialidade especialidade)
    {
        validarDadosObrigatoriosStatic(pacienteId, medicoId, dataHora, especialidade);
    }

    private static void validarDadosObrigatoriosStatic(PacienteId pacienteId,
                                                        MedicoId medicoId,
                                                        LocalDateTime dataHora,
                                                        Especialidade especialidade)
    {
        if (pacienteId == null) {
            throw new ConsultaBusinessException("Paciente é obrigatório");
        }
        if (medicoId == null) {
            throw new ConsultaBusinessException("Médico é obrigatório");
        }
        if (dataHora == null) {
            throw new ConsultaBusinessException("Data e hora são obrigatórias");
        }
        if (especialidade == null) {
            throw new ConsultaBusinessException("Especialidade é obrigatória");
        }
    }

    private void validarDataFutura(LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new ConsultaBusinessException("Data da consulta deve ser futura");
        }
    }

    public ConsultaId getId() { return id; }
    public PacienteId getPacienteId() { return pacienteId; }
    public MedicoId getMedicoId() { return medicoId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Especialidade getEspecialidade() { return especialidade; }
    public StatusConsulta getStatus() { return status; }
    public TipoConsulta getTipoConsulta() { return tipoConsulta; }
    public Prioridade getPrioridade() { return prioridade; }
    public Long getTriagemId() { return triagemId; }

    public void setId(ConsultaId id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consulta consulta = (Consulta) o;
        return Objects.equals(id, consulta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}