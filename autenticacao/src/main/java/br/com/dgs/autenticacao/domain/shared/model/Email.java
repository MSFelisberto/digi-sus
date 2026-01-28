package br.com.dgs.autenticacao.domain.shared.model;

import br.com.dgs.autenticacao.domain.shared.exception.SharedBusinessException;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final String value;

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new SharedBusinessException("E-mail não pode ser vazio");
        }

        String emailTrimmed = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(emailTrimmed).matches()) {
            throw new SharedBusinessException("Formato de e-mail inválido");
        }

        this.value = emailTrimmed;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
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
