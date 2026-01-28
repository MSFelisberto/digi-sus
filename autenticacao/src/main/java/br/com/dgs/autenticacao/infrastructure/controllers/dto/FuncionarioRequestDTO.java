package br.com.dgs.autenticacao.infrastructure.controllers.dto;

import br.com.dgs.autenticacao.application.dto.CadastrarFuncionarioCommand;
import br.com.dgs.autenticacao.application.dto.EspecialidadeDTO;
import br.com.dgs.autenticacao.domain.funcionario.model.TipoFuncionario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record FuncionarioRequestDTO(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        @NotNull(message = "Tipo é obrigatório")
        TipoFuncionario tipo,

        @NotBlank(message = "Nome completo é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nomeCompleto,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "CPF deve estar no formato 11122233344 ou 111.222.333-44")
        String cpf,

        @Size(max = 20, message = "CRM deve ter no máximo 20 caracteres")
        String crm,

        @Size(max = 20, message = "COREN deve ter no máximo 20 caracteres")
        String coren,

        @Valid
        EspecialidadeRequestDTO especialidade
) {
    public CadastrarFuncionarioCommand toCommand() {
        EspecialidadeDTO especialidadeDTO = null;
        if (especialidade != null) {
            especialidadeDTO = new EspecialidadeDTO(
                    especialidade.nome(),
                    especialidade.codigo()
            );
        }

        return new CadastrarFuncionarioCommand(
                email, senha, tipo, nomeCompleto, cpf.replaceAll("[^0-9]", ""),
                crm, coren, especialidadeDTO
        );
    }
}
