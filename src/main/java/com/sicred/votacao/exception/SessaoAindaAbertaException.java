package com.sicred.votacao.exception;

public class SessaoAindaAbertaException extends RuntimeException {
    public SessaoAindaAbertaException(String message) {
        super(message);
    }
}