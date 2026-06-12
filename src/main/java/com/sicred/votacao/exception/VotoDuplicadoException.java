package com.sicred.votacao.exception;

public class VotoDuplicadoException extends RuntimeException {
    public VotoDuplicadoException() { super(); }
    public VotoDuplicadoException(String message) { super(message); }
}
