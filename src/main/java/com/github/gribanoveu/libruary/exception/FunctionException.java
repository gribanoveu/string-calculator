package com.github.gribanoveu.libruary.exception;

/**
 * Ошибка при вычислении выражения функции.
 * @author Evgeny Gribanov
 * @version 16.04.2024
 */
public class FunctionException extends ArithmeticException {
    public FunctionException(String message) {
        super(message);
    }
}
