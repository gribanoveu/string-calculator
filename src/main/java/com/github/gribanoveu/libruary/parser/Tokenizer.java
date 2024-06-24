package com.github.gribanoveu.libruary.parser;


import com.github.gribanoveu.libruary.entity.Token;

import java.util.List;

/**
 * Разобрать выражение на токены.
 * @author Evgeny Gribanov
 * @version 18.04.2024
 */
public interface Tokenizer {
    List<Token> parseExpression(String incomeExpression);
}
