package com.sicred.votacao.exception;

public class PautaNotFoundException extends RuntimeException {
    public PautaNotFoundException() { super(); }
    public PautaNotFoundException(String message) { super(message); }
}
