package br.com.dgs.autenticacao.domain.paciente.model;

import br.com.dgs.autenticacao.domain.paciente.exception.PacienteBusinessException;
import br.com.dgs.autenticacao.domain.shared.model.Email;
import br.com.dgs.autenticacao.domain.shared.model.Senha;

import java.time.LocalDate;
import java.util.Objects;

public class Paciente {
    private PacienteId id;
    private final Email email;
    private final Senha senha;
    private final String nomeCompleto;
    private final String cpf;
    private final LocalDate dataNascimento;
    private final String telefone;
    private final Endereco endereco;
    private final boolean ativo;

    public Paciente(Email email, Senha senha, String nomeCompleto, String cpf,
                    LocalDate dataNascimento, String telefone, Endereco endereco) {
        validarDadosObrigatorios(email, senha, nomeCompleto, cpf, dataNascimento, telefone, endereco);
        validarIdade(dataNascimento);
        validarCpf(cpf);

        this.email = email;
        this.senha = senha;
        this.nomeCompleto = nomeCompleto.trim();
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone.trim();
        this.endereco = endereco;
        this.ativo = true;
    }

    public Paciente(PacienteId id, Email email, Senha senha, String nomeCompleto, String cpf,
                    LocalDate dataNascimento, String telefone, Endereco endereco, boolean ativo) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.endereco = endereco;
        this.ativo = ativo;
    }

    public boolean podeMarcarConsulta() {
        return this.ativo && calcularIdade() >= 0;
    }

    public void inativar() {
        if (!this.ativo) {
            throw new PacienteBusinessException("Paciente já está inativo");
        }
    }

    public int calcularIdade() {
        return LocalDate.now().getYear() - this.dataNascimento.getYear();
    }

    private void validarDadosObrigatorios(Email email, Senha senha, String nomeCompleto,
                                          String cpf, LocalDate dataNascimento, String telefone, Endereco endereco) {
        if (email == null) throw new PacienteBusinessException("E-mail é obrigatório");
        if (senha == null) throw new PacienteBusinessException("Senha é obrigatória");
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty())
            throw new PacienteBusinessException("Nome completo é obrigatório");
        if (cpf == null || cpf.trim().isEmpty())
            throw new PacienteBusinessException("CPF é obrigatório");
        if (dataNascimento == null)
            throw new PacienteBusinessException("Data de nascimento é obrigatória");
        if (telefone == null || telefone.trim().isEmpty())
            throw new PacienteBusinessException("Telefone é obrigatório");
        if (endereco == null)
            throw new PacienteBusinessException("Endereço é obrigatório");
    }

    private void validarIdade(LocalDate dataNascimento) {
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new PacienteBusinessException("Data de nascimento deve ser no passado");
        }

        int idade = LocalDate.now().getYear() - dataNascimento.getYear();
        if (idade > 120) {
            throw new PacienteBusinessException("Idade inválida");
        }
    }

    private void validarCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            throw new PacienteBusinessException("CPF deve ter 11 dígitos");
        }
    }

    public PacienteId getId() { return id; }
    public Email getEmail() { return email; }
    public Senha getSenha() { return senha; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getCpf() { return cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getTelefone() { return telefone; }
    public Endereco getEndereco() { return endereco; }
    public boolean isAtivo() { return ativo; }

    public void setId(PacienteId id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return Objects.equals(id, paciente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
