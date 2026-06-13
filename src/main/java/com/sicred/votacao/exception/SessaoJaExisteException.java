package com.sicred.votacao.exception;

public class SessaoJaExisteException extends RuntimeException {
    public SessaoJaExisteException(String message) { super(message); }
}
