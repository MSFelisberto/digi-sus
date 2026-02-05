package br.com.dgs.exames.application.dto;

public record CriarTipoExameCommand(
        String nome,
        String codigo,
        String descricao,
        String preparacao
) {}
