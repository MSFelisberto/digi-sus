package br.com.dgs.triagem.domain.model;

import java.util.Objects;
import java.util.UUID;

public class TriagemId {
    private final String value;

    public TriagemId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da triagem não pode ser nulo ou vazio");
        }
        this.value = value;
    }

    public static TriagemId generate() {
        return new TriagemId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriagemId that = (TriagemId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "TriagemId{" + value + '}';
    }
}
