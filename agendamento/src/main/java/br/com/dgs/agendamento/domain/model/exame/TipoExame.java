package br.com.dgs.agendamento.domain.model.exame;

import br.com.dgs.agendamento.domain.exception.ExameBusinessException;

import java.util.Objects;

public class TipoExame {
    private TipoExameId id;
    private final String nome;
    private final String codigo;
    private final String descricao;
    private final String preparacao;
    private boolean ativo;

    public TipoExame(String nome, String codigo, String descricao, String preparacao) {
        if (nome == null || nome.trim().isEmpty())
            throw new ExameBusinessException("Nome do tipo de exame é obrigatório");
        if (codigo == null || codigo.trim().isEmpty())
            throw new ExameBusinessException("Código do tipo de exame é obrigatório");

        this.nome = nome.trim().toUpperCase();
        this.codigo = codigo.trim().toUpperCase();
        this.descricao = descricao;
        this.preparacao = preparacao;
        this.ativo = true;
    }

    public TipoExame(TipoExameId id, String nome, String codigo,
                     String descricao, String preparacao, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.descricao = descricao;
        this.preparacao = preparacao;
        this.ativo = ativo;
    }

    public TipoExameId getId() { return id; }
    public String getNome() { return nome; }
    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public String getPreparacao() { return preparacao; }
    public boolean isAtivo() { return ativo; }

    public void setId(TipoExameId id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoExame that = (TipoExame) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}