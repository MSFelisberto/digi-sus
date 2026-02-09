package br.com.dgs.triagem.infrastructure.messaging.adapters;

import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.TriagemAtendimentoDTO;
import br.com.dgs.commons.dtos.TriagemHistoricoDTO;
import br.com.dgs.triagem.application.ports.outbound.TriagemMessagingService;
import br.com.dgs.triagem.domain.model.Triagem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TriagemMessagingServiceImpl implements TriagemMessagingService {

    private final RabbitTemplate rabbitTemplate;

    public TriagemMessagingServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void enviarParaAtendimento(Triagem triagem) {
        Long triagemIdNumerico = (long) Math.abs(triagem.getId().getValue().hashCode());
        
        TriagemAtendimentoDTO dto = new TriagemAtendimentoDTO(
                triagem.getPacienteId().getValue(),
                triagemIdNumerico,
                triagem.getDadosClinicos(),
                triagem.getConduta(),
                triagem.getEspecialidade(),
                triagem.getPrioridade().name()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_TRIAGEM_ATENDIMENTO,
                dto
        );

        log.info("Mensagem enviada para fila de atendimento. Triagem ID: {}", triagem.getId().getValue());
    }

    @Override
    public void enviarParaHistorico(Triagem triagem) {
        Long triagemIdNumerico = (long) Math.abs(triagem.getId().getValue().hashCode());
        
        TriagemHistoricoDTO dto = new TriagemHistoricoDTO(
                triagemIdNumerico,
                triagem.getPacienteId().getValue(),
                triagem.getFuncionarioId().getValue(),
                triagem.getPressaoArterial(),
                triagem.getTemperatura(),
                triagem.getBatimentoCardiaco(),
                triagem.getConduta(),
                triagem.getEspecialidade(),
                triagem.getPrioridade().name()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_TRIAGEM_HISTORICO,
                dto
        );

        log.info("Mensagem enviada para fila de histórico. Triagem ID: {}", triagem.getId().getValue());
    }
}
