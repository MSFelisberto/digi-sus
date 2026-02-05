package br.com.dgs.agendamento.infrastructure.controllers.handlers;

import br.com.dgs.agendamento.domain.exception.*;
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

    @ExceptionHandler(AgendaNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAgendaNotFoundException(AgendaNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Agenda Não Encontrada", "message", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AgendaBusinessException.class)
    public ResponseEntity<Map<String, String>> handleAgendaBusinessException(AgendaBusinessException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Regra de Negócio Violada", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HorarioIndisponivelException.class)
    public ResponseEntity<Map<String, String>> handleHorarioIndisponivelException(HorarioIndisponivelException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Horário Indisponível", "message", ex.getMessage()),
                HttpStatus.CONFLICT
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
