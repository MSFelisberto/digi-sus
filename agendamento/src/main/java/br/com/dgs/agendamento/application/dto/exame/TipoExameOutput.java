package br.com.dgs.agendamento.application.dto.exame;

public record TipoExameOutput(
        Long id,
        String nome,
        String codigo,
        String descricao,
        String preparacao,
        boolean ativo
) {}