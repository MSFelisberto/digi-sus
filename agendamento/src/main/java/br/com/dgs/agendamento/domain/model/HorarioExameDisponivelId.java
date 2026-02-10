package br.com.dgs.agendamento.domain.model;

import java.util.Objects;

public class HorarioExameDisponivelId {
    private final Long value;

    public HorarioExameDisponivelId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ID do horário de exame deve ser um número positivo");
        }
        this.value = value;
    }

    public Long getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HorarioExameDisponivelId that = (HorarioExameDisponivelId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }
}
