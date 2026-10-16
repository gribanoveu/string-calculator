package com.github.gribanoveu.libruary.entity;

/**
 * Представление отдельного токена.
 * @author Evgeny Gribanov
 * @version 16.10.2026
 */
public record Token(TokenType type, String value) {
    /**
     * Конец выражения. Возвращается буфером токенов, когда токены закончились.
     */
    public static final Token EOF = new Token(TokenType.EOF, "");
}
