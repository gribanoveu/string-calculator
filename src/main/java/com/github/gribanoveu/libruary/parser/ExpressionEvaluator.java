package com.github.gribanoveu.libruary.parser;


import com.github.gribanoveu.libruary.util.TokenBuffer;

/**
 * Интерфейс, который реализует механизм для вычисления значения.
 * Принимает объект TokenBuffer, который представляет собой список токенов.
 * <code>
 *  {
 *   Token {type: NUMBER, value: 2},
 *   Token {type: OP_PLUS, value: +},
 *   Token {type: NUMBER, value: 3},
 *   Token {type: EOF, value: }
 *  }
 * </code>
 *
 * @author Evgeny Gribanov
 * @version 18.04.2024
 */
public interface ExpressionEvaluator {
    double calculateExpression(TokenBuffer buffer);
}
