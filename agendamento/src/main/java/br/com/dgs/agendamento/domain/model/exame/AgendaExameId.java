package br.com.dgs.agendamento.domain.model.exame;

import java.util.Objects;

public class AgendaExameId {
    private final Long value;

    public AgendaExameId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ID da agenda de exame deve ser um número positivo");
        }
        this.value = value;
    }

    public Long getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendaExameId that = (AgendaExameId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }
}