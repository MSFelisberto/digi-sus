package br.com.dgs.exames.application.ports.inbound;

import br.com.dgs.exames.application.dto.CriarSolicitacaoExameCommand;
import br.com.dgs.exames.application.dto.CriarSolicitacaoExamePorNomeCommand;
import br.com.dgs.exames.application.dto.ListarSolicitacoesQuery;
import br.com.dgs.exames.application.dto.SolicitacaoExameOutput;

import java.util.List;

public interface SolicitacaoExameUseCase {
    SolicitacaoExameOutput criarSolicitacao(CriarSolicitacaoExameCommand command);
    SolicitacaoExameOutput criarSolicitacaoPorNomeExame(CriarSolicitacaoExamePorNomeCommand command);
    List<SolicitacaoExameOutput> listarPorPaciente(ListarSolicitacoesQuery query);
    List<SolicitacaoExameOutput> listarPorAtendimento(Long atendimentoId);
    void cancelarSolicitacao(Long solicitacaoId);
}
