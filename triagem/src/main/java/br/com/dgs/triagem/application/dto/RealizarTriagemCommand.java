package br.com.dgs.triagem.application.dto;

public record RealizarTriagemCommand(
        Long pacienteId,
        Long funcionarioId,
        String pressaoArterial,
        Double temperatura,
        Integer batimentoCardiaco,
        String conduta
) {
}
