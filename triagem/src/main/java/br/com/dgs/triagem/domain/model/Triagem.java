package br.com.dgs.triagem.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Triagem {
    private TriagemId triagemId;
    private PacienteId pacienteId;
    private FuncionarioId funcionarioId;

    private BigDecimal pressaoSistolica;
    private BigDecimal pressaoDiastolica;
    private BigDecimal temperatura;
    private Integer batimentoCardiaco;

    private String conduta;

    public Triagem(PacienteId pacienteId,
                   FuncionarioId funcionarioId,
                   BigDecimal pressaoSistolica,
                   BigDecimal pressaoDiastolica,
                   BigDecimal temperatura,
                   Integer batimentoCardiaco,
                   String conduta) {

        validar(pacienteId, funcionarioId, pressaoSistolica, pressaoDiastolica, temperatura, batimentoCardiaco, conduta);

        this.triagemId = TriagemId.gerar();
        this.pacienteId = pacienteId;
        this.funcionarioId = funcionarioId;
        this.pressaoSistolica = pressaoSistolica;
        this.pressaoDiastolica = pressaoDiastolica;
        this.temperatura = temperatura;
        this.batimentoCardiaco = batimentoCardiaco;
        this.conduta = conduta;
    }

    public Triagem(TriagemId triagemId,
                   PacienteId pacienteId,
                   FuncionarioId funcionarioId,
                   BigDecimal pressaoSistolica,
                   BigDecimal pressaoDiastolica,
                   BigDecimal temperatura,
                   Integer batimentoCardiaco,
                   String conduta) {

        validar(pacienteId, funcionarioId, pressaoSistolica, pressaoDiastolica, temperatura, batimentoCardiaco, conduta);

        this.triagemId = triagemId;
        this.pacienteId = pacienteId;
        this.funcionarioId = funcionarioId;
        this.pressaoSistolica = pressaoSistolica;
        this.pressaoDiastolica = pressaoDiastolica;
        this.temperatura = temperatura;
        this.batimentoCardiaco = batimentoCardiaco;
        this.conduta = conduta;
    }

    private void validar(PacienteId pacienteId,
                         FuncionarioId funcionarioId,
                         BigDecimal sistolica,
                         BigDecimal diastolica,
                         BigDecimal temperatura,
                         Integer batimento,
                         String conduta) {

        if (pacienteId == null) throw new RuntimeException("Paciente é obrigatório");
        if (funcionarioId == null) throw new RuntimeException("Funcionário é obrigatório");
        if (sistolica == null || diastolica == null) throw new RuntimeException("Pressão arterial é obrigatória");
        if (temperatura == null) throw new RuntimeException("Temperatura é obrigatória");
        if (batimento == null) throw new RuntimeException("Batimento cardíaco é obrigatório");
        if (conduta == null || conduta.isBlank()) throw new RuntimeException("Conduta é obrigatória");

        if (temperatura.compareTo(new BigDecimal("25")) < 0 || temperatura.compareTo(new BigDecimal("45")) > 0)
            throw new RuntimeException("Temperatura inválida");

        if (batimento < 20 || batimento > 250)
            throw new RuntimeException("Batimento inválido");
    }

    public TriagemId getTriagemId() { return triagemId; }
    public PacienteId getPacienteId() { return pacienteId; }
    public FuncionarioId getFuncionarioId() { return funcionarioId; }
    public BigDecimal getPressaoSistolica() { return pressaoSistolica; }
    public BigDecimal getPressaoDiastolica() { return pressaoDiastolica; }
    public BigDecimal getTemperatura() { return temperatura; }
    public Integer getBatimentoCardiaco() { return batimentoCardiaco; }
    public String getConduta() { return conduta; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triagem triagem = (Triagem) o;
        return Objects.equals(triagemId, triagem.triagemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(triagemId);
    }
}
