package br.com.dgs.autenticacao.domain.funcionario.model;

import java.util.Objects;

public class FuncionarioId {
    private final Long value;

    public FuncionarioId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ID do funcionário deve ser um número positivo");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuncionarioId that = (FuncionarioId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
