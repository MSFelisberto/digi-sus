package br.com.dgs.autenticacao.application.dto;

import br.com.dgs.autenticacao.domain.funcionario.model.TipoFuncionario;

import java.time.LocalDateTime;

public record FuncionarioOutput(
        Long id,
        String email,
        TipoFuncionario tipo,
        String nomeCompleto,
        String cpf,
        String crm,
        String coren,
        EspecialidadeDTO especialidade,
        boolean ativo,
        LocalDateTime dataCadastro
) {}
