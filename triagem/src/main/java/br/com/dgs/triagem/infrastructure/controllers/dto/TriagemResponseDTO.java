package br.com.dgs.triagem.infrastructure.controllers.dto;

public record TriagemResponseDTO(
        String triagemId,
        Long pacienteId,
        Long funcionarioId,
        String pressaoArterial,
        Double temperatura,
        Integer batimentoCardiaco,
        String conduta,
        String especialidade,
        String prioridade,
        String mensagem
) {
}
