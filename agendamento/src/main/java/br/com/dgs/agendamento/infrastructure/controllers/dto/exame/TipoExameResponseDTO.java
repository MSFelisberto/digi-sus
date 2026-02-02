package br.com.dgs.agendamento.infrastructure.controllers.dto.exame;

public record TipoExameResponseDTO(
        Long id,
        String nome,
        String codigo,
        String descricao,
        String preparacao,
        boolean ativo
) {}