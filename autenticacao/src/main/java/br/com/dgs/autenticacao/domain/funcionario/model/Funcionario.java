package br.com.dgs.autenticacao.domain.funcionario.model;

import br.com.dgs.autenticacao.domain.funcionario.exception.FuncionarioBusinessException;
import br.com.dgs.autenticacao.domain.shared.model.Email;
import br.com.dgs.autenticacao.domain.shared.model.Senha;

import java.time.LocalDateTime;
import java.util.Objects;

public class Funcionario {
    private FuncionarioId id;
    private final Email email;
    private final Senha senha;
    private final TipoFuncionario tipo;
    private final String nomeCompleto;
    private final String cpf;
    private final String crm;
    private final String coren;
    private final Especialidade especialidade;
    private final boolean ativo;
    private final LocalDateTime dataCadastro;

    public Funcionario(Email email, Senha senha, TipoFuncionario tipo, String nomeCompleto,
                       String cpf, String crm, String coren, Especialidade especialidade) {
        validarDadosObrigatorios(email, senha, tipo, nomeCompleto, cpf);
        validarDadosEspecificosPorTipo(tipo, crm, coren, especialidade);
        validarCpf(cpf);

        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.nomeCompleto = nomeCompleto.trim();
        this.cpf = cpf;
        this.crm = crm;
        this.coren = coren;
        this.especialidade = especialidade;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    public Funcionario(FuncionarioId id, Email email, Senha senha, TipoFuncionario tipo,
                       String nomeCompleto, String cpf, String crm, String coren,
                       Especialidade especialidade, boolean ativo, LocalDateTime dataCadastro) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.crm = crm;
        this.coren = coren;
        this.especialidade = especialidade;
        this.ativo = ativo;
        this.dataCadastro = dataCadastro;
    }

    public boolean podeRealizarProcedimentos() {
        return this.ativo && (this.tipo == TipoFuncionario.MEDICO || this.tipo == TipoFuncionario.ENFERMEIRO);
    }

    public boolean isAdmin() {
        return this.tipo == TipoFuncionario.ADMIN;
    }

    public boolean isMedico() {
        return this.tipo == TipoFuncionario.MEDICO;
    }

    public boolean isEnfermeiro() {
        return this.tipo == TipoFuncionario.ENFERMEIRO;
    }

    public void inativar() {
        if (!this.ativo) {
            throw new FuncionarioBusinessException("Funcionário já está inativo");
        }
    }

    private void validarDadosObrigatorios(Email email, Senha senha, TipoFuncionario tipo,
                                          String nomeCompleto, String cpf) {
        if (email == null) throw new FuncionarioBusinessException("E-mail é obrigatório");
        if (senha == null) throw new FuncionarioBusinessException("Senha é obrigatória");
        if (tipo == null) throw new FuncionarioBusinessException("Tipo é obrigatório");
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty())
            throw new FuncionarioBusinessException("Nome completo é obrigatório");
        if (cpf == null || cpf.trim().isEmpty())
            throw new FuncionarioBusinessException("CPF é obrigatório");
    }

    private void validarDadosEspecificosPorTipo(TipoFuncionario tipo, String crm,
                                                String coren, Especialidade especialidade) {
        switch (tipo) {
            case MEDICO:
                if (crm == null || crm.trim().isEmpty()) {
                    throw new FuncionarioBusinessException("CRM é obrigatório para médicos");
                }
                if (especialidade == null) {
                    throw new FuncionarioBusinessException("Especialidade é obrigatória para médicos");
                }
                if (coren != null && !coren.trim().isEmpty()) {
                    throw new FuncionarioBusinessException("Médico não pode ter COREN");
                }
                break;

            case ENFERMEIRO:
                if (coren == null || coren.trim().isEmpty()) {
                    throw new FuncionarioBusinessException("COREN é obrigatório para enfermeiros");
                }
                if (crm != null && !crm.trim().isEmpty()) {
                    throw new FuncionarioBusinessException("Enfermeiro não pode ter CRM");
                }
                if (especialidade != null) {
                    throw new FuncionarioBusinessException("Enfermeiro não pode ter especialidade");
                }
                break;

            case ADMIN:
                if (crm != null && !crm.trim().isEmpty()) {
                    throw new FuncionarioBusinessException("Admin não pode ter CRM");
                }
                if (coren != null && !coren.trim().isEmpty()) {
                    throw new FuncionarioBusinessException("Admin não pode ter COREN");
                }
                if (especialidade != null) {
                    throw new FuncionarioBusinessException("Admin não pode ter especialidade");
                }
                break;
        }
    }

    private void validarCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            throw new FuncionarioBusinessException("CPF deve ter 11 dígitos");
        }
    }

    public FuncionarioId getId() { return id; }
    public Email getEmail() { return email; }
    public Senha getSenha() { return senha; }
    public TipoFuncionario getTipo() { return tipo; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getCpf() { return cpf; }
    public String getCrm() { return crm; }
    public String getCoren() { return coren; }
    public Especialidade getEspecialidade() { return especialidade; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setId(FuncionarioId id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionario that = (Funcionario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
