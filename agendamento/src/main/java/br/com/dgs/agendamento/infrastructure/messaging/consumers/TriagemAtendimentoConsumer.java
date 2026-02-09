package br.com.dgs.agendamento.infrastructure.messaging.consumers;

import br.com.dgs.agendamento.application.dto.ConsultaOutput;
import br.com.dgs.agendamento.application.dto.CriarConsultaTriagemCommand;
import br.com.dgs.agendamento.application.ports.inbound.ConsultaTriagemUseCase;
import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.TriagemAtendimentoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TriagemAtendimentoConsumer {

    private final ConsultaTriagemUseCase consultaTriagemUseCase;

    public TriagemAtendimentoConsumer(ConsultaTriagemUseCase consultaTriagemUseCase) {
        this.consultaTriagemUseCase = consultaTriagemUseCase;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_TRIAGEM_ATENDIMENTO)
    public void receberTriagemAtendimento(TriagemAtendimentoDTO dto) {
        log.info("[TRIAGEM-ATENDIMENTO] Mensagem recebida. Triagem ID: {}, Paciente ID: {}",
                dto.triagemId(), dto.pacienteId());

        try {
            CriarConsultaTriagemCommand command = new CriarConsultaTriagemCommand(
                    dto.pacienteId(),
                    dto.triagemId(),
                    dto.dadosClinicos(),
                    dto.conduta(),
                    dto.especialidade(),
                    dto.prioridade()
            );

            ConsultaOutput output = consultaTriagemUseCase.criarConsultaTriagem(command);

            log.info("[TRIAGEM-ATENDIMENTO] Consulta criada com sucesso. Consulta ID: {}, Tipo: {}, Triagem: {}",
                    output.id(), output.tipoConsulta(), dto.triagemId());
        } catch (Exception e) {
            log.error("[TRIAGEM-ATENDIMENTO] Erro ao processar triagem ID: {}. Erro: {}",
                    dto.triagemId(), e.getMessage(), e);
        }
    }
}
