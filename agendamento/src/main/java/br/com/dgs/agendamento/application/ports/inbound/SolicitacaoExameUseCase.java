package br.com.dgs.agendamento.application.ports.inbound;

import br.com.dgs.agendamento.application.dto.exame.CriarSolicitacaoExameCommand;
import br.com.dgs.agendamento.application.dto.exame.ListarSolicitacoesQuery;
import br.com.dgs.agendamento.application.dto.exame.SolicitacaoExameOutput;

import java.util.List;

public interface SolicitacaoExameUseCase {
    SolicitacaoExameOutput criarSolicitacao(CriarSolicitacaoExameCommand command);
    List<SolicitacaoExameOutput> listarPorPaciente(ListarSolicitacoesQuery query);
    void cancelarSolicitacao(Long solicitacaoId);
}