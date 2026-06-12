package com.sicred.votacao.exception;

public class SessaoEncerradaException extends RuntimeException {
    public SessaoEncerradaException() { super(); }
    public SessaoEncerradaException(String message) { super(message); }
}
