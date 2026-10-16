package com.github.gribanoveu.libruary.util;

import com.github.gribanoveu.libruary.entity.Token;
import com.github.gribanoveu.libruary.entity.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Evgeny Gribanov
 * @version 16.10.2026
 */
class TokenBufferTest {
    private static final Token NUMBER = new Token(TokenType.NUMBER, "2");

    @Test
    void nextReturnsEofWhenTokensAreOver() {
        var buffer = new TokenBuffer(List.of(NUMBER, Token.EOF));

        assertThat(buffer.next()).isEqualTo(NUMBER);
        assertThat(buffer.next()).isEqualTo(Token.EOF);
        assertThat(buffer.next())
                .as("За концом списка токенов возвращается EOF, а не исключение")
                .isEqualTo(Token.EOF);
    }

    @Test
    void backKeepsPositionInsideBuffer() {
        var buffer = new TokenBuffer(List.of(NUMBER));

        buffer.back();
        buffer.back();

        assertThat(buffer.getTokenPosition()).isZero();
        assertThat(buffer.next()).isEqualTo(NUMBER);
    }

    @Test
    void getTokenPositionCountsReadTokens() {
        var buffer = new TokenBuffer(List.of(NUMBER, new Token(TokenType.OP_PLUS, "+"), NUMBER));

        buffer.next();
        buffer.next();

        assertThat(buffer.getTokenPosition()).isEqualTo(2);
        buffer.back();
        assertThat(buffer.getTokenPosition()).isEqualTo(1);
    }

    @Test
    void getCharPositionPointsToTheLastReadToken() {
        var buffer = new TokenBuffer(List.of(
                new Token(TokenType.NUMBER, "12"),
                new Token(TokenType.OP_PLUS, "+"),
                new Token(TokenType.NUMBER, "3")));

        buffer.next();
        buffer.next();
        buffer.next();

        assertThat(buffer.getCharPosition())
                .as("Токен '3' начинается с третьего символа выражения 12+3")
                .isEqualTo(3);
    }
}
