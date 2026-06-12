package com.sicred.votacao.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PautaNotFoundException.class)
    public ResponseEntity<ApiError> handlePautaNotFound(PautaNotFoundException ex, WebRequest request) {
        log.warn("Pauta not found: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SessaoNotFoundException.class)
    public ResponseEntity<ApiError> handleSessaoNotFound(SessaoNotFoundException ex, WebRequest request) {
        log.warn("Sessao not found: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SessaoJaExisteException.class)
    public ResponseEntity<ApiError> handleSessaoJaExiste(SessaoJaExisteException ex, WebRequest request) {
        log.warn("Sessao already exists: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SessaoEncerradaException.class)
    public ResponseEntity<ApiError> handleSessaoEncerrada(SessaoEncerradaException ex, WebRequest request) {
        log.warn("Sessao encerrada: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VotoDuplicadoException.class)
    public ResponseEntity<ApiError> handleVotoDuplicado(VotoDuplicadoException ex, WebRequest request) {
        log.warn("Voto duplicado: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CpfInvalidoException.class)
    public ResponseEntity<ApiError> handleCpfInvalido(CpfInvalidoException ex, WebRequest request) {
        log.warn("CPF inválido: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AssociadoNaoHabilitadoException.class)
    public ResponseEntity<ApiError> handleAssociadoNaoHabilitado(AssociadoNaoHabilitadoException ex, WebRequest request) {
        log.warn("Associado não habilitado: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ServicoExternoIndisponivelException.class)
    public ResponseEntity<ApiError> handleServicoIndisponivel(ServicoExternoIndisponivelException ex, WebRequest request) {
        log.error("Servico externo indisponivel: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  org.springframework.http.HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Validation failed: {}", errors);
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(org.springframework.http.HttpStatus.BAD_REQUEST.value())
                .error(org.springframework.http.HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Dados inválidos")
                .errors(errors)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected error")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
