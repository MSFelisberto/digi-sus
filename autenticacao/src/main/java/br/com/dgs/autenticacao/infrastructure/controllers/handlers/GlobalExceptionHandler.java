package br.com.dgs.autenticacao.infrastructure.controllers.handlers;

import br.com.dgs.autenticacao.domain.funcionario.exception.FuncionarioBusinessException;
import br.com.dgs.autenticacao.domain.funcionario.exception.FuncionarioNotFoundException;
import br.com.dgs.autenticacao.domain.paciente.exception.PacienteBusinessException;
import br.com.dgs.autenticacao.domain.paciente.exception.PacienteNotFoundException;
import br.com.dgs.autenticacao.domain.shared.exception.SharedBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PacienteBusinessException.class)
    public ResponseEntity<Map<String, String>> handlePacienteBusinessException(PacienteBusinessException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Erro de Paciente", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(PacienteNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePacienteNotFoundException(PacienteNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Paciente não encontrado", "message", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(FuncionarioBusinessException.class)
    public ResponseEntity<Map<String, String>> handleFuncionarioBusinessException(FuncionarioBusinessException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Erro de Funcionário", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(FuncionarioNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFuncionarioNotFoundException(FuncionarioNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Funcionário não encontrado", "message", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(SharedBusinessException.class)
    public ResponseEntity<Map<String, String>> handleSharedBusinessException(SharedBusinessException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Erro de validação", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Credenciais inválidas", "message", "E-mail ou senha incorretos"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                Map.of("error", "Argumento inválido", "message", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
