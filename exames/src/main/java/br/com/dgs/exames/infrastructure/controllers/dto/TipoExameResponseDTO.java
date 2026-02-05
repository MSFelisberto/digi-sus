package br.com.dgs.exames.infrastructure.controllers.dto;

public record TipoExameResponseDTO(
        Long id,
        String nome,
        String codigo,
        String descricao,
        String preparacao,
        boolean ativo
) {}
