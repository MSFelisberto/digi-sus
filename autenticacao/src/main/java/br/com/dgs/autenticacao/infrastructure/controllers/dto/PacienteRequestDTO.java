package br.com.dgs.autenticacao.infrastructure.controllers.dto;

import br.com.dgs.autenticacao.application.dto.CadastrarPacienteCommand;
import br.com.dgs.autenticacao.application.dto.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PacienteRequestDTO(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        @NotBlank(message = "Nome completo é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nomeCompleto,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "CPF deve estar no formato 11122233344 ou 111.222.333-44")
        String cpf,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate dataNascimento,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}",
                message = "Formato de telefone inválido")
        String telefone,

        @NotNull(message = "Endereço é obrigatório")
        @Valid
        EnderecoRequestDTO endereco
) {
    public CadastrarPacienteCommand toCommand() {
        EnderecoDTO enderecoDTO = new EnderecoDTO(
                endereco.logradouro(),
                endereco.numero(),
                endereco.complemento(),
                endereco.bairro(),
                endereco.cidade(),
                endereco.estado(),
                endereco.cep()
        );

        return new CadastrarPacienteCommand(
                email, senha, nomeCompleto, cpf.replaceAll("[^0-9]", ""),
                dataNascimento, telefone, enderecoDTO
        );
    }
}
