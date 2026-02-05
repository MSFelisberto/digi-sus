package br.com.dgs.historico.application.ports.inbound;

import br.com.dgs.historico.application.dto.*;

import java.util.List;

public interface HistoricoUseCase {
    HistoricoOutput registrarHistorico(RegistrarHistoricoCommand command);
    HistoricoOutput atualizarHistorico(AtualizarHistoricoCommand command);
    void cancelarHistorico(CancelarHistoricoCommand command);
    List<HistoricoOutput> listarHistoricoPorPaciente(ListarHistoricoQuery query);
    HistoricoOutput buscarPorConsultaId(Long consultaId);
    HistoricoOutput registrarAtendimentoFinalizado(RegistrarAtendimentoFinalizadoCommand command);
}
