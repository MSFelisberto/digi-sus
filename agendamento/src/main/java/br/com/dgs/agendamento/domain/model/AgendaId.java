package br.com.dgs.agendamento.domain.model;

import java.util.Objects;

public class AgendaId {
    private final Long value;

    public AgendaId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ID da agenda deve ser um número positivo");
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
        AgendaId that = (AgendaId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "AgendaId{" + value + '}';
    }
}
