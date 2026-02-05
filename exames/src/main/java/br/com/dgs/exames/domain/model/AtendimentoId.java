package br.com.dgs.exames.domain.model;

import java.util.Objects;

public class AtendimentoId {
    private final Long value;

    public AtendimentoId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ID do atendimento deve ser um número positivo");
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
        AtendimentoId that = (AtendimentoId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
