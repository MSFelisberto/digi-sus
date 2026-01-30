package br.com.dgs.triagem.domain.model;

import java.util.UUID;

public class TriagemId {

    private String triagemId;

    public void setTriagemId(String triagemId) {
        this.triagemId = UUID.randomUUID().toString();
    }
}