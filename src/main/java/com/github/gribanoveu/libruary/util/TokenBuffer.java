package com.github.gribanoveu.libruary.util;


import com.github.gribanoveu.libruary.entity.Token;

import java.util.List;

/**
 * Буфер токенов.
 * Для перемещения по массиву токенов.
 * @author Evgeny Gribanov
 * @version 17.04.2024
 */
public class TokenBuffer {
    private int tokenPosition;

    public List<Token> tokens;

    public TokenBuffer(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Передвинутся по буферу токенов на одну ячейку вперед.
     * @return следующий токен.
     */
    public Token next() {
        return tokens.get(tokenPosition++);
    }

    /**
     * Передвинутся по буферу токенов на одну ячейку назад.
     */
    public void back() {
        tokenPosition--;
    }

    /**
     * Получить текущую позицию токена.
     * @return текущий токен.
     */
    public int getTokenPosition() {
        return tokenPosition;
    }
}
