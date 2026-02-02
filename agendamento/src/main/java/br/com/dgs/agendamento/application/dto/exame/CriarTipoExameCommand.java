package br.com.dgs.agendamento.application.dto.exame;

public record CriarTipoExameCommand(
        String nome,
        String codigo,
        String descricao,
        String preparacao
) {}