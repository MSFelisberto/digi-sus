package br.com.dgs.atendimento.domain.model;

import java.util.Objects;

public class ConsultaId {
    private final Long value;

    public ConsultaId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ID da consulta deve ser positivo");
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
        ConsultaId that = (ConsultaId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
