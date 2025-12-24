package com.br.elton.tecnologia.coupon.api.exception;

/**
 * Exceção custom simples para ser lançada como:
 * throw new MinhaExcecaoCustom("mensagem");
 */
public class BussinesException extends RuntimeException {

    public BussinesException(String message) {
        super(message);
    }

    public BussinesException(String message, Throwable cause) {
        super(message, cause);
    }
}

