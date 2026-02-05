package br.com.dgs.atendimento.infrastructure.controllers.handlers;

import br.com.dgs.atendimento.domain.exception.AtendimentoBusinessException;
import br.com.dgs.atendimento.domain.exception.AtendimentoNotFoundException;
import br.com.dgs.atendimento.domain.exception.ConsultaInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AtendimentoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAtendimentoNotFoundException(AtendimentoNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Atendimento Não Encontrado", "message", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AtendimentoBusinessException.class)
    public ResponseEntity<Map<String, String>> handleAtendimentoBusinessException(AtendimentoBusinessException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Regra de Negócio Violada", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConsultaInvalidaException.class)
    public ResponseEntity<Map<String, String>> handleConsultaInvalidaException(ConsultaInvalidaException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Consulta Inválida", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Argumento Inválido", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
