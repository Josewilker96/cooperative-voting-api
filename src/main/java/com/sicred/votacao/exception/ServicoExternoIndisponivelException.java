package com.sicred.votacao.exception;

public class ServicoExternoIndisponivelException extends RuntimeException {
    public ServicoExternoIndisponivelException() { super(); }
    public ServicoExternoIndisponivelException(String message) { super(message); }
}
