package br.com.dgs.autenticacao.domain.paciente.model;

import br.com.dgs.autenticacao.domain.paciente.exception.PacienteBusinessException;

import java.util.Objects;

public class Endereco {
    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final String cep;

    public Endereco(String logradouro, String numero, String complemento,
                    String bairro, String cidade, String estado, String cep) {
        validarDadosObrigatorios(logradouro, numero, bairro, cidade, estado, cep);

        this.logradouro = logradouro.trim();
        this.numero = numero.trim();
        this.complemento = complemento != null ? complemento.trim() : null;
        this.bairro = bairro.trim();
        this.cidade = cidade.trim();
        this.estado = estado.trim().toUpperCase();
        this.cep = cep.replaceAll("[^0-9]", "");
    }

    private void validarDadosObrigatorios(String logradouro, String numero,
                                          String bairro, String cidade, String estado, String cep) {
        if (logradouro == null || logradouro.trim().isEmpty())
            throw new PacienteBusinessException("Logradouro é obrigatório");
        if (numero == null || numero.trim().isEmpty())
            throw new PacienteBusinessException("Número é obrigatório");
        if (bairro == null || bairro.trim().isEmpty())
            throw new PacienteBusinessException("Bairro é obrigatório");
        if (cidade == null || cidade.trim().isEmpty())
            throw new PacienteBusinessException("Cidade é obrigatória");
        if (estado == null || estado.trim().isEmpty())
            throw new PacienteBusinessException("Estado é obrigatório");
        if (cep == null || cep.trim().isEmpty())
            throw new PacienteBusinessException("CEP é obrigatório");

        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new PacienteBusinessException("CEP deve ter 8 dígitos");
        }
    }

    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getCep() { return cep; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(logradouro, endereco.logradouro) &&
                Objects.equals(numero, endereco.numero) &&
                Objects.equals(bairro, endereco.bairro) &&
                Objects.equals(cidade, endereco.cidade) &&
                Objects.equals(estado, endereco.estado) &&
                Objects.equals(cep, endereco.cep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logradouro, numero, bairro, cidade, estado, cep);
    }
}
