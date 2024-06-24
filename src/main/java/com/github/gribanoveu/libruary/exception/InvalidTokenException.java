package com.github.gribanoveu.libruary.exception;


import com.github.gribanoveu.libruary.util.TokenBuffer;

/**
 * Ошибка при разборе токенов.
 * @author Evgeny Gribanov
 * @version 16.04.2024
 */
public class InvalidTokenException extends ArithmeticException {
    public InvalidTokenException(TokenBuffer buffer) {
        super("Unexpected token at position: %s".formatted(buffer.getTokenPosition()));
    }
}