package br.com.dgs.exames.application.dto;

public record TipoExameOutput(
        Long id,
        String nome,
        String codigo,
        String descricao,
        String preparacao,
        boolean ativo
) {}
