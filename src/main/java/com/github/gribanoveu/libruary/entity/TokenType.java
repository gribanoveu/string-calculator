package com.github.gribanoveu.libruary.entity;

/**
 * Типы токенов.
 * @author Evgeny Gribanov
 * @version 17.04.2024
 */
public enum TokenType {
    LEFT_BRACKET, RIGHT_BRACKET,
    OP_PLUS, OP_MINUS, OP_MULTIPLICATION, OP_DIVISION,
    NUMBER, FUNCTION, COMMA,
    EOF
}
