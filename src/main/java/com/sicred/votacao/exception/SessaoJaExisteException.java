package com.sicred.votacao.exception;

public class SessaoJaExisteException extends RuntimeException {
    public SessaoJaExisteException() { super(); }
    public SessaoJaExisteException(String message) { super(message); }
}
