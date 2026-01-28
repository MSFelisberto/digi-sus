package br.com.dgs.autenticacao.domain.funcionario.model;

import br.com.dgs.autenticacao.domain.funcionario.exception.FuncionarioBusinessException;

import java.util.Objects;

public class Especialidade {
    private final String nome;
    private final String codigo;

    public Especialidade(String nome, String codigo) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new FuncionarioBusinessException("Nome da especialidade é obrigatório");
        }
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new FuncionarioBusinessException("Código da especialidade é obrigatório");
        }

        this.nome = nome.trim().toUpperCase();
        this.codigo = codigo.trim().toUpperCase();
    }

    public String getNome() { return nome; }
    public String getCodigo() { return codigo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Especialidade that = (Especialidade) o;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return nome;
    }
}
