package br.com.dgs.triagem.domain.model;

import br.com.dgs.triagem.domain.exception.TriagemBusinessException;

import java.util.Objects;

public class Triagem {
    private TriagemId id;
    private PacienteId pacienteId;
    private FuncionarioId funcionarioId;
    private String pressaoArterial;
    private Double temperatura;
    private Integer batimentoCardiaco;
    private String conduta;

    public Triagem(PacienteId pacienteId,
                   FuncionarioId funcionarioId,
                   String pressaoArterial,
                   Double temperatura,
                   Integer batimentoCardiaco,
                   String conduta) {
        validarDadosObrigatorios(pacienteId, funcionarioId, pressaoArterial, temperatura, batimentoCardiaco, conduta);
        validarDadosClinicos(temperatura, batimentoCardiaco);

        this.id = TriagemId.generate();
        this.pacienteId = pacienteId;
        this.funcionarioId = funcionarioId;
        this.pressaoArterial = pressaoArterial;
        this.temperatura = temperatura;
        this.batimentoCardiaco = batimentoCardiaco;
        this.conduta = conduta;
    }

    private void validarDadosObrigatorios(PacienteId pacienteId,
                                          FuncionarioId funcionarioId,
                                          String pressaoArterial,
                                          Double temperatura,
                                          Integer batimentoCardiaco,
                                          String conduta) {
        if (pacienteId == null) {
            throw new TriagemBusinessException("Paciente é obrigatório");
        }
        if (funcionarioId == null) {
            throw new TriagemBusinessException("Funcionário é obrigatório");
        }
        if (pressaoArterial == null || pressaoArterial.trim().isEmpty()) {
            throw new TriagemBusinessException("Pressão arterial é obrigatória");
        }
        if (temperatura == null) {
            throw new TriagemBusinessException("Temperatura é obrigatória");
        }
        if (batimentoCardiaco == null) {
            throw new TriagemBusinessException("Batimento cardíaco é obrigatório");
        }
        if (conduta == null || conduta.trim().isEmpty()) {
            throw new TriagemBusinessException("Conduta é obrigatória");
        }
    }

    private void validarDadosClinicos(Double temperatura, Integer batimentoCardiaco) {
        if (temperatura < 30.0 || temperatura > 45.0) {
            throw new TriagemBusinessException("Temperatura deve estar entre 30°C e 45°C");
        }
        if (batimentoCardiaco < 30 || batimentoCardiaco > 250) {
            throw new TriagemBusinessException("Batimento cardíaco deve estar entre 30 e 250 bpm");
        }
    }

    public String getDadosClinicos() {
        return String.format("Pressão: %s, Temperatura: %.1f°C, Batimentos: %d bpm",
                pressaoArterial, temperatura, batimentoCardiaco);
    }

    public TriagemId getId() { return id; }
    public PacienteId getPacienteId() { return pacienteId; }
    public FuncionarioId getFuncionarioId() { return funcionarioId; }
    public String getPressaoArterial() { return pressaoArterial; }
    public Double getTemperatura() { return temperatura; }
    public Integer getBatimentoCardiaco() { return batimentoCardiaco; }
    public String getConduta() { return conduta; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triagem triagem = (Triagem) o;
        return Objects.equals(id, triagem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
