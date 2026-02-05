package br.com.dgs.exames.infrastructure.messaging.consumers;

import br.com.dgs.exames.application.dto.CriarSolicitacaoExamePorNomeCommand;
import br.com.dgs.exames.application.ports.inbound.SolicitacaoExameUseCase;
import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.SolicitacaoExameEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SolicitacaoExameEventConsumer {

    private final SolicitacaoExameUseCase solicitacaoExameUseCase;

    public SolicitacaoExameEventConsumer(SolicitacaoExameUseCase solicitacaoExameUseCase) {
        this.solicitacaoExameUseCase = solicitacaoExameUseCase;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_ATENDIMENTO_EXAME_SOLICITAR)
    public void processarSolicitacaoExame(SolicitacaoExameEventDTO event) {
        log.info("Recebido evento de solicitação de exame do atendimento. AtendimentoId: {}, TipoExame: {}",
                event.atendimentoId(), event.tipoExame());

        try {
            CriarSolicitacaoExamePorNomeCommand command = new CriarSolicitacaoExamePorNomeCommand(
                    event.pacienteId(),
                    event.medicoId(),
                    event.tipoExame(),
                    event.atendimentoId(),
                    event.consultaId(),
                    event.prioridade(),
                    event.observacoes()
            );

            solicitacaoExameUseCase.criarSolicitacaoPorNomeExame(command);

            log.info("Solicitação de exame criada com sucesso a partir do atendimento {}", event.atendimentoId());
        } catch (Exception e) {
            log.error("Erro ao processar solicitação de exame do atendimento {}: {}",
                    event.atendimentoId(), e.getMessage(), e);
        }
    }
}
