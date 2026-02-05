package br.com.dgs.exames.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_tipos_exame")
@Getter
@Setter
public class TipoExameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String codigo;
    private String descricao;
    private String preparacao;
    private boolean ativo;
}
