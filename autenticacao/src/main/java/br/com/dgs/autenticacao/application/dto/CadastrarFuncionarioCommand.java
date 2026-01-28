package br.com.dgs.autenticacao.application.dto;

import br.com.dgs.autenticacao.domain.funcionario.model.TipoFuncionario;

public record CadastrarFuncionarioCommand(
        String email,
        String senha,
        TipoFuncionario tipo,
        String nomeCompleto,
        String cpf,
        String crm,
        String coren,
        EspecialidadeDTO especialidade
) {}
