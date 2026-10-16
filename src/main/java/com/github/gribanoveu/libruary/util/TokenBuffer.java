package com.github.gribanoveu.libruary.util;


import com.github.gribanoveu.libruary.entity.Token;

import java.util.List;

/**
 * Буфер токенов.
 * Для перемещения по массиву токенов.
 * @author Evgeny Gribanov
 * @version 16.10.2026
 */
public class TokenBuffer {
    private final List<Token> tokens;
    private int tokenPosition;

    public TokenBuffer(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Передвинутся по буферу токенов на одну ячейку вперед.
     * @return следующий токен, либо EOF, если токены закончились.
     */
    public Token next() {
        return tokenPosition < tokens.size() ? tokens.get(tokenPosition++) : Token.EOF;
    }

    /**
     * Передвинутся по буферу токенов на одну ячейку назад.
     */
    public void back() {
        if (tokenPosition > 0) {
            tokenPosition--;
        }
    }

    /**
     * Получить текущую позицию токена.
     * @return текущий токен.
     */
    public int getTokenPosition() {
        return tokenPosition;
    }

    /**
     * Позиция в исходном выражении, с которой начинается последний прочитанный токен.
     * Пробелы удаляются при токенизации, поэтому позиция считается по длинам токенов.
     * @return позиция в символах.
     */
    public int getCharPosition() {
        var lastTokenIndex = Math.max(0, Math.min(tokenPosition - 1, tokens.size() - 1));
        var position = 0;
        for (var i = 0; i < lastTokenIndex; i++) {
            position += tokens.get(i).value().length();
        }
        return position;
    }
}
