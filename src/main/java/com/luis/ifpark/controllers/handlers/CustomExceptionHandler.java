package com.luis.ifpark.controllers.handlers;

import com.luis.ifpark.dtos.CustomError;
import com.luis.ifpark.dtos.ValidationError;
import com.luis.ifpark.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<CustomError> cpfJaCadastrado(CpfJaCadastradoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<CustomError> emailJaCadastrado(EmailJaCadastradoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> database(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError err = new ValidationError(Instant.now(), status.value(), "Dados inválidos", request.getRequestURI());
        
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(org.springframework.security.authentication.DisabledException.class)
    public ResponseEntity<CustomError> disabledException(org.springframework.security.authentication.DisabledException e, HttpServletRequest request) {
        System.out.println("=== HANDLER DISABLED EXCEPTION CHAMADO ===");
        System.out.println("Mensagem: " + e.getMessage());

        HttpStatus status = HttpStatus.FORBIDDEN;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomError> badCredentials(BadCredentialsException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                "Email ou senha incorretos.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<CustomError> accessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<CustomError> internalAuthenticationServiceException(InternalAuthenticationServiceException e,
            HttpServletRequest request) {

        System.out.println("=== HANDLER INTERNAL AUTH SERVICE EXCEPTION ===");

        if (e.getCause() instanceof DisabledException) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            CustomError err = new CustomError(
                    Instant.now(),
                    status.value(),
                    e.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(status).body(err);
        }

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                "Erro interno de autenticação.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomError> authenticationException(AuthenticationException e, HttpServletRequest request) {

        System.out.println("=== HANDLER AUTHENTICATION EXCEPTION CHAMADO ===");
        System.out.println("Tipo da exceção: " + e.getClass().getName());
        System.out.println("Mensagem: " + e.getMessage());
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                "Acesso não autorizado. Token não fornecido ou inválido.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<CustomError> dataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException e,
                                                              HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = "Violação de integridade de dados. Campo duplicado (Matrícula, CPF ou Email) ou vínculo inválido.";

        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<CustomError> regraDeNegocio(RegraDeNegocioException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomError> illegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> globalError(Exception e, HttpServletRequest request) {
        e.printStackTrace();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                "Ocorreu um erro interno inesperado no servidor. Contate o suporte.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }


}
