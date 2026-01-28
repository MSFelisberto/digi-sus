package br.com.dgs.autenticacao.application.dto;

import java.time.LocalDate;

public record CadastrarPacienteCommand(
        String email,
        String senha,
        String nomeCompleto,
        String cpf,
        LocalDate dataNascimento,
        String telefone,
        EnderecoDTO endereco
) {}
