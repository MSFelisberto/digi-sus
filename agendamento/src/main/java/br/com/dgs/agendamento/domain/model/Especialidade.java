package br.com.dgs.agendamento.domain.model;

import java.util.Objects;

public class Especialidade {
    private final String value;

    public Especialidade(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Especialidade não pode ser vazia");
        }
        this.value = value.trim().toUpperCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Especialidade that = (Especialidade) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
