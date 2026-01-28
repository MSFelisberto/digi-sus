package br.com.dgs.autenticacao.infrastructure.controllers.dto;

import br.com.dgs.autenticacao.application.dto.PacienteOutput;

import java.time.LocalDate;

public record PacienteResponseDTO(
        Long id,
        String email,
        String nomeCompleto,
        String cpf,
        LocalDate dataNascimento,
        String telefone,
        EnderecoResponseDTO endereco,
        boolean ativo
) {
    public static PacienteResponseDTO fromOutput(PacienteOutput output) {
        EnderecoResponseDTO enderecoDTO = new EnderecoResponseDTO(
                output.endereco().logradouro(),
                output.endereco().numero(),
                output.endereco().complemento(),
                output.endereco().bairro(),
                output.endereco().cidade(),
                output.endereco().estado(),
                output.endereco().cep()
        );

        return new PacienteResponseDTO(
                output.id(),
                output.email(),
                output.nomeCompleto(),
                output.cpf(),
                output.dataNascimento(),
                output.telefone(),
                enderecoDTO,
                output.ativo()
        );
    }
}
