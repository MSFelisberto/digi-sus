package br.com.dgs.historico.domain.model;

import java.util.Objects;

public class HistoricoId {
    private final Long value;

    public HistoricoId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ID do histórico deve ser um número positivo");
        }
        this.value = value;
    }

    public Long getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricoId that = (HistoricoId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }
}
