package br.com.dgs.autenticacao.application.dto;

import java.time.LocalDate;

public record PacienteOutput(
        Long id,
        String email,
        String nomeCompleto,
        String cpf,
        LocalDate dataNascimento,
        String telefone,
        EnderecoDTO endereco,
        boolean ativo
) {}
