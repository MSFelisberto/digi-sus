package br.com.dgs.agendamento.infrastructure.controllers.handlers;

import br.com.dgs.agendamento.domain.exception.AuthorizationException;
import br.com.dgs.agendamento.domain.exception.ConsultaBusinessException;
import br.com.dgs.agendamento.domain.exception.ConsultaNotFoundException;
import br.com.dgs.agendamento.domain.exception.PacienteNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationException(AuthorizationException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Acesso Negado", "message", ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ConsultaNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleConsultaNotFoundException(ConsultaNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Consulta Não Encontrada", "message", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(PacienteNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePacienteNotFoundException(PacienteNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Paciente Não Encontrado", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConsultaBusinessException.class)
    public ResponseEntity<Map<String, String>> handleConsultaBusinessException(ConsultaBusinessException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Regra de Negócio Violada", "message", ex.getMessage()),
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
