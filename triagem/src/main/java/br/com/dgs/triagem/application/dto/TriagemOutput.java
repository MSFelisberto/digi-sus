package br.com.dgs.triagem.application.dto;

public record TriagemOutput(
        String triagemId,
        Long pacienteId,
        Long funcionarioId,
        String pressaoArterial,
        Double temperatura,
        Integer batimentoCardiaco,
        String conduta,
        String mensagem
) {
}
