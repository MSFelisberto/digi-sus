package br.com.dgs.atendimento.domain.model;

import java.util.Objects;

public class Anamnese {
    private final String value;

    public Anamnese(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Anamnese não pode ser vazia");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anamnese anamnese = (Anamnese) o;
        return Objects.equals(value, anamnese.value);
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
