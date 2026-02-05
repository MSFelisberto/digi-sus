package br.com.dgs.agendamento.application.ports.inbound;

import br.com.dgs.agendamento.application.dto.*;

import java.util.List;

public interface AgendamentoUseCase {
    ConsultaOutput agendarConsulta(AgendarConsultaCommand command);
    ConsultaOutput reagendarConsulta(ReagendarConsultaCommand command);
    void cancelarConsulta(CancelarConsultaCommand command);
    List<ConsultaOutput> listarConsultasPorPaciente(ListarConsultasQuery query);
    List<ConsultaOutput> listarConsultasFuturas(ListarConsultasFuturasQuery query);
    ConsultaOutput buscarPorId(Long consultaId);
    ConsultaOutput marcarComoRealizada(Long consultaId);
}
