package br.com.dgs.historico.infrastructure.messaging;

import br.com.dgs.commons.config.RabbitConfig;
import br.com.dgs.commons.dtos.TriagemHistoricoDTO;
import br.com.dgs.historico.infrastructure.persistence.entity.TriagemHistoricoEntity;
import br.com.dgs.historico.infrastructure.persistence.repository.TriagemHistoricoJPARepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TriagemHistoricoConsumer {

    private final TriagemHistoricoJPARepository repository;

    public TriagemHistoricoConsumer(TriagemHistoricoJPARepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_TRIAGEM_HISTORICO)
    public void consumeTriagemHistorico(TriagemHistoricoDTO triagemDTO) {
        log.info("[TRIAGEM-HISTORICO] Recebido evento de triagem - Triagem ID: {}, Paciente: {}, Funcionário: {}",
                triagemDTO.triagemId(), triagemDTO.pacienteId(), triagemDTO.funcionarioId());

        try {
            processarTriagem(triagemDTO);
        } catch (Exception e) {
            log.error("[TRIAGEM-HISTORICO] Erro ao processar triagem: {}", e.getMessage(), e);
        }
    }

    private void processarTriagem(TriagemHistoricoDTO triagemDTO) {
        if (repository.existsByTriagemId(triagemDTO.triagemId())) {
            log.warn("[TRIAGEM-HISTORICO] Triagem ID {} já existe no histórico. Ignorando duplicata.", 
                    triagemDTO.triagemId());
            return;
        }

        TriagemHistoricoEntity entity = new TriagemHistoricoEntity();
        entity.setTriagemId(triagemDTO.triagemId());
        entity.setPacienteId(triagemDTO.pacienteId());
        entity.setFuncionarioId(triagemDTO.funcionarioId());
        entity.setPressaoArterial(triagemDTO.pressaoArterial());
        entity.setTemperatura(triagemDTO.temperatura());
        entity.setBatimentoCardiaco(triagemDTO.batimentoCardiaco());
        entity.setConduta(triagemDTO.conduta());
        entity.setDataRegistro(LocalDateTime.now());

        TriagemHistoricoEntity saved = repository.save(entity);

        log.info("[TRIAGEM-HISTORICO] Triagem registrada no histórico com sucesso:");
        log.info("  - ID Histórico: {}", saved.getId());
        log.info("  - Triagem ID: {}", saved.getTriagemId());
        log.info("  - Paciente ID: {}", saved.getPacienteId());
        log.info("  - Funcionário ID: {}", saved.getFuncionarioId());
        log.info("  - Pressão Arterial: {}", saved.getPressaoArterial());
        log.info("  - Temperatura: {}°C", saved.getTemperatura());
        log.info("  - Batimento Cardíaco: {} bpm", saved.getBatimentoCardiaco());
        log.info("  - Data Registro: {}", saved.getDataRegistro());
    }
}
