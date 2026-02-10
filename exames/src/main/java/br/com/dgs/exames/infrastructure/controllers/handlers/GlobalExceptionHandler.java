package br.com.dgs.exames.infrastructure.controllers.handlers;

import br.com.dgs.exames.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TipoExameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTipoExameNotFoundException(TipoExameNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Tipo de Exame Não Encontrado", "message", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(SolicitacaoExameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSolicitacaoExameNotFoundException(SolicitacaoExameNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Solicitação de Exame Não Encontrada", "message", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(PacienteNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePacienteNotFoundException(PacienteNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Paciente Não Encontrado", "message", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ExameBusinessException.class)
    public ResponseEntity<Map<String, String>> handleExameBusinessException(ExameBusinessException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Regra de Negócio Violada", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationException(AuthorizationException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Não Autorizado", "message", ex.getMessage()),
                HttpStatus.FORBIDDEN
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
