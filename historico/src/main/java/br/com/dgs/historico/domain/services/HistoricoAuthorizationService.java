package br.com.dgs.historico.domain.services;

import br.com.dgs.historico.domain.exception.AuthorizationException;
import br.com.dgs.historico.domain.model.PacienteId;

public class HistoricoAuthorizationService {

    public void validarAcessoHistoricoPaciente(
            PacienteId pacienteIdSolicitado,
            Long usuarioAutenticadoId,
            String role) {

        if (isRoleMedicoOuEnfermeiro(role)) {
            return;
        }

        if (isRolePaciente(role)) {
            PacienteId pacienteIdAutenticado = new PacienteId(usuarioAutenticadoId);

            if (!pacienteIdSolicitado.equals(pacienteIdAutenticado)) {
                throw new AuthorizationException(
                        String.format(
                                "Acesso negado. Paciente %d não pode visualizar histórico do paciente %d.",
                                usuarioAutenticadoId,
                                pacienteIdSolicitado.getValue()
                        )
                );
            }
            return;
        }

        throw new AuthorizationException(
                "Acesso negado. Usuário não possui permissão para acessar histórico médico."
        );
    }

    private boolean isRoleMedicoOuEnfermeiro(String role) {
        return "ROLE_MEDICO".equals(role) || "ROLE_ENFERMEIRO".equals(role);
    }

    private boolean isRolePaciente(String role) {
        return "ROLE_PACIENTE".equals(role);
    }

}
