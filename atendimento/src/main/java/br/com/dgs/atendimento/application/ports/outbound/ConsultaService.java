package br.com.dgs.atendimento.application.ports.outbound;

import br.com.dgs.atendimento.domain.model.ConsultaId;

public interface ConsultaService {
    ConsultaInfo buscarConsulta(ConsultaId consultaId);
    void marcarComoRealizada(ConsultaId consultaId);
    void marcarComoEmAtendimento(ConsultaId consultaId);

    record ConsultaInfo(
            Long id,
            Long pacienteId,
            Long medicoId,
            String status
    ) {}
}
