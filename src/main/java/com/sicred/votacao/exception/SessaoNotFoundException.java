package com.sicred.votacao.exception;

public class SessaoNotFoundException extends RuntimeException {
    public SessaoNotFoundException() { super(); }
    public SessaoNotFoundException(String message) { super(message); }
}
