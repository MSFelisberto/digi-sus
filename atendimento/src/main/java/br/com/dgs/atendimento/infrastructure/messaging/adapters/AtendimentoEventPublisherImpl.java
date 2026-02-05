package br.com.dgs.atendimento.infrastructure.messaging.adapters;

import br.com.dgs.atendimento.application.ports.outbound.AtendimentoEventPublisher;
import br.com.dgs.atendimento.domain.model.Atendimento;
import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.AtendimentoFinalizadoEventDTO;
import br.com.dgs.commons.dtos.SolicitacaoExameEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AtendimentoEventPublisherImpl implements AtendimentoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AtendimentoEventPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicarAtendimentoFinalizado(Atendimento atendimento) {
        AtendimentoFinalizadoEventDTO dto = new AtendimentoFinalizadoEventDTO(
                atendimento.getId().getValue(),
                atendimento.getConsultaId().getValue(),
                atendimento.getPacienteId().getValue(),
                atendimento.getMedicoId().getValue(),
                atendimento.getAnamnese().getValue(),
                atendimento.getCondutaMedica().getValue(),
                atendimento.getDataHoraInicio(),
                atendimento.getDataHoraFim()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_ATENDIMENTO_FINALIZADO,
                dto
        );

        log.info("Evento de atendimento finalizado publicado. AtendimentoId: {}, ConsultaId: {}",
                atendimento.getId().getValue(), atendimento.getConsultaId().getValue());
    }

    @Override
    public void publicarExameSolicitado(Long atendimentoId, Long consultaId, Long pacienteId, Long medicoId,
                                         String tipoExame, String prioridade, String observacoes) {
        SolicitacaoExameEventDTO dto = new SolicitacaoExameEventDTO(
                atendimentoId,
                consultaId,
                pacienteId,
                medicoId,
                tipoExame,
                prioridade,
                observacoes
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_ATENDIMENTO_EXAME_SOLICITAR,
                dto
        );

        log.info("Evento de solicitação de exame publicado. AtendimentoId: {}, TipoExame: {}",
                atendimentoId, tipoExame);
    }
}
