package com.github.gribanoveu.libruary.parser;

import com.github.gribanoveu.libruary.entity.Token;
import com.github.gribanoveu.libruary.entity.TokenType;
import com.github.gribanoveu.libruary.exception.FunctionException;
import com.github.gribanoveu.libruary.util.FunctionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Evgeny Gribanov
 * @version 19.04.2024
 */
class ExpressionTokenizerTest {
    private Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        var functionManager = new FunctionManager();
        functionManager.registerFunction("function", x -> 0.0);
        tokenizer = new ExpressionTokenizer(functionManager);
    }

    @Test
    void tokenizeSimpleExpression() {
        var expression = "2";
        var tokenizerResult = tokenizer.parseExpression(expression);
        assertThat(tokenizerResult)
                .as("Выражение не имеет ожидаемых токенов")
                .isNotEmpty()
                .hasSize(2)
                .containsExactly(
                        new Token(TokenType.NUMBER, "2"),
                        new Token(TokenType.EOF, "")
                );
    }

    @Test
    void tokenizeShortNumberExpression() {
        var expression = "123+45";
        var tokenizerResult = tokenizer.parseExpression(expression);
        assertThat(tokenizerResult)
                .as("Выражение не имеет ожидаемых токенов")
                .isNotEmpty()
                .hasSize(4)
                .containsExactly(
                        new Token(TokenType.NUMBER, "123"),
                        new Token(TokenType.OP_PLUS, "+"),
                        new Token(TokenType.NUMBER, "45"),
                        new Token(TokenType.EOF, "")
                );
    }

    @Test
    void tokenizeSimpleExpressionSpaces() {
        var expression = "2 +2";
        var tokenizerResult = tokenizer.parseExpression(expression);
        assertThat(tokenizerResult)
                .as("Выражение не имеет ожидаемых токенов")
                .isNotEmpty()
                .hasSize(4)
                .containsExactly(
                        new Token(TokenType.NUMBER, "2"),
                        new Token(TokenType.OP_PLUS, "+"),
                        new Token(TokenType.NUMBER, "2"),
                        new Token(TokenType.EOF, "")
                );
    }

    @Test
    void tokenizeOperators() {
        var expression = "+-*/";
        var tokenizerResult = tokenizer.parseExpression(expression);
        assertThat(tokenizerResult)
                .as("Выражение не имеет ожидаемых токенов")
                .isNotEmpty()
                .hasSize(5)
                .containsExactly(
                        new Token(TokenType.OP_PLUS, "+"),
                        new Token(TokenType.OP_MINUS, "-"),
                        new Token(TokenType.OP_MULTIPLICATION, "*"),
                        new Token(TokenType.OP_DIVISION, "/"),
                        new Token(TokenType.EOF, "")
                );
    }

    @Test
    void tokenizeBrackets() {
        var expression = "()";
        var tokenizerResult = tokenizer.parseExpression(expression);
        assertThat(tokenizerResult)
                .as("Выражение не имеет ожидаемых токенов")
                .isNotEmpty()
                .hasSize(3)
                .containsExactly(
                        new Token(TokenType.LEFT_BRACKET, "("),
                        new Token(TokenType.RIGHT_BRACKET, ")"),
                        new Token(TokenType.EOF, "")
                );
    }

    @Test
    void tokenizeComma() {
        var expression = ",";
        var tokenizerResult = tokenizer.parseExpression(expression);
        assertThat(tokenizerResult)
                .as("Выражение не имеет ожидаемых токенов")
                .isNotEmpty()
                .hasSize(2)
                .containsExactly(
                        new Token(TokenType.COMMA, ","),
                        new Token(TokenType.EOF, "")
                );
    }

    @Test
    void tokenizeEmptyString() {
        var expression = "";
        var tokenizerResult = tokenizer.parseExpression(expression);
        assertThat(tokenizerResult)
                .as("Выражение не имеет ожидаемых токенов")
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(
                        new Token(TokenType.EOF, "")
                );
    }

    @Test
    void tokenizeEmptySpacedString() {
        var expression = "    ";
        var tokenizerResult = tokenizer.parseExpression(expression);
        assertThat(tokenizerResult)
                .as("Выражение не имеет ожидаемых токенов")
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(
                        new Token(TokenType.EOF, "")
                );
    }

    @Test
    void tokenizeFunction() {
        var expression = "function(2)";
        var tokenizerResult = tokenizer.parseExpression(expression);
        assertThat(tokenizerResult)
                .as("Выражение не имеет ожидаемых токенов")
                .isNotEmpty()
                .hasSize(5)
                .containsExactly(
                        new Token(TokenType.FUNCTION, "function"),
                        new Token(TokenType.LEFT_BRACKET, "("),
                        new Token(TokenType.NUMBER, "2"),
                        new Token(TokenType.RIGHT_BRACKET, ")"),
                        new Token(TokenType.EOF, "")
                );
    }

    @Test
    void tokenizeThrowException() {
        assertThatThrownBy(() -> tokenizer.parseExpression("2&"))
                .as("Ожидался неверный токен")
                .isInstanceOf(ArithmeticException.class)
                .hasMessageContaining("Unexpected character: &");
    }

    @Test
    void tokenizeThrowFunctionException() {
        assertThatThrownBy(() -> tokenizer.parseExpression("2+rand(256)"))
                .as("Ожидался неверный токен")
                .isInstanceOf(FunctionException.class)
                .hasMessageContaining("Function not found: rand");
    }
}