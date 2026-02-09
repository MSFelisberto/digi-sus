package br.com.dgs.agendamento.application.dto;

public record CriarConsultaTriagemCommand(
        Long pacienteId,
        Long triagemId,
        String dadosClinicos,
        String conduta,
        String especialidade,
        String prioridade
) {
}
