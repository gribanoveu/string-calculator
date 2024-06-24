package com.github.gribanoveu.libruary.parser;

import com.github.gribanoveu.libruary.entity.Token;
import com.github.gribanoveu.libruary.entity.TokenType;
import com.github.gribanoveu.libruary.exception.InvalidTokenException;
import com.github.gribanoveu.libruary.function.CalculateFunction;
import com.github.gribanoveu.libruary.util.FunctionManager;
import com.github.gribanoveu.libruary.util.TokenBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Evgeny Gribanov
 * @version 19.04.2024
 */
class SyntaxEvaluatorTest {
    private ExpressionEvaluator syntaxEvaluator;
    private TokenBuffer tokenBuffer;

    @BeforeEach
    void setUp() {
        var functionManager = new FunctionManager();
        functionManager.registerFunction("min", new MinimalValueFunctions());
        functionManager.registerFunction("pow", new PowValueFunctions());
        syntaxEvaluator = new SyntaxEvaluator(functionManager);
    }

    @Nested
    @DisplayName("Арифметические операции")
    class SimpleOperations {
        @Test
        void evaluateSum() {
            var buffer = new TokenBuffer(List.of(
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(4.0);
        }

        @Test
        void evaluateDifference() {
            var buffer = new TokenBuffer(List.of(
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.OP_MINUS, "-"),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(1.0);
        }

        @Test
        void evaluateMultiplication() {
            var buffer = new TokenBuffer(List.of(
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.OP_MULTIPLICATION, "*"),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(6.0);
        }

        @Test
        void evaluateDivision() {
            var buffer = new TokenBuffer(List.of(
                    new Token(TokenType.NUMBER, "6"),
                    new Token(TokenType.OP_DIVISION, "/"),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(3.0);
        }
    }

    @Nested
    @DisplayName("Приоритет операций")
    class OperationPriority {
        @Test
        void priorityMultiplication() {
            var buffer = new TokenBuffer(List.of( // 2 + 3 * 4 = 14
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.OP_MULTIPLICATION, "*"),
                    new Token(TokenType.NUMBER, "4"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(14.0);
        }

        @Test
        void priorityBrackets() {
            var buffer = new TokenBuffer(List.of( // (2 + 3) * 4 = 20
                    new Token(TokenType.LEFT_BRACKET, "("),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.RIGHT_BRACKET, ")"),
                    new Token(TokenType.OP_MULTIPLICATION, "*"),
                    new Token(TokenType.NUMBER, "4"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(20.0);
        }

        @Test
        void priorityDivision() {
            var buffer = new TokenBuffer(List.of( // 6 / 2 - 1 = 2
                    new Token(TokenType.NUMBER, "6"),
                    new Token(TokenType.OP_DIVISION, "/"),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_MINUS, "-"),
                    new Token(TokenType.NUMBER, "1"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(2.0);
        }

        @Test
        void priorityDivisionBrackets() {
            var buffer = new TokenBuffer(List.of( // 6 / (2 - 1) = 6
                    new Token(TokenType.NUMBER, "6"),
                    new Token(TokenType.OP_DIVISION, "/"),
                    new Token(TokenType.LEFT_BRACKET, "("),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_MINUS, "-"),
                    new Token(TokenType.NUMBER, "1"),
                    new Token(TokenType.RIGHT_BRACKET, ")"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(6.0);
        }

        @Test
        void priorityMultiDivBeforePlusMinus() {
            var buffer = new TokenBuffer(List.of( // 3 + 4 * 5 - 6 / 2 = 20
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.NUMBER, "4"),
                    new Token(TokenType.OP_MULTIPLICATION, "*"),
                    new Token(TokenType.NUMBER, "5"),
                    new Token(TokenType.OP_MINUS, "-"),
                    new Token(TokenType.NUMBER, "6"),
                    new Token(TokenType.OP_DIVISION, "/"),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(20.0);
        }

        @Test
        void priorityMultiDivBeforePlusMinusBrackets() {
            var buffer = new TokenBuffer(List.of( // 3 + (4 * 5 - 6) / 2 = 10
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.LEFT_BRACKET, "("),
                    new Token(TokenType.NUMBER, "4"),
                    new Token(TokenType.OP_MULTIPLICATION, "*"),
                    new Token(TokenType.NUMBER, "5"),
                    new Token(TokenType.OP_MINUS, "-"),
                    new Token(TokenType.NUMBER, "6"),
                    new Token(TokenType.RIGHT_BRACKET, ")"),
                    new Token(TokenType.OP_DIVISION, "/"),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(10.0);
        }

        @Test
        void exceptionWhenNoCloseBracket() {
            var buffer = new TokenBuffer(List.of( // (2 + 3 * 4 = ошибка
                    new Token(TokenType.LEFT_BRACKET, "("),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.OP_MULTIPLICATION, "*"),
                    new Token(TokenType.NUMBER, "4"),
                    new Token(TokenType.EOF, "")
            ));

            assertThatThrownBy(() -> {
                syntaxEvaluator.calculateExpression(buffer);
            })
                    .as("Ожидался неверный токен")
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining("Unexpected token at position: 7");
        }

        @Test
        void exceptionWhenUnexpectedTokenInFunctionArguments() {
            var buffer = new TokenBuffer(List.of( // 2 + min(3, 9) = 5
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.FUNCTION, "min"),
                    new Token(TokenType.LEFT_BRACKET, "("),
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.NUMBER, "&"),
                    new Token(TokenType.NUMBER, "9"),
                    new Token(TokenType.RIGHT_BRACKET, ")"),
                    new Token(TokenType.EOF, "")
            ));

            assertThatThrownBy(() -> {
                syntaxEvaluator.calculateExpression(buffer);
            })
                    .as("Ожидался неверный токен")
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining("Unexpected token at position: 6");
        }
    }

    @Nested
    @DisplayName("Унарный минус")
    class Unary {
        @Test
        void evaluateSimpleUnary() {
            var buffer = new TokenBuffer(List.of( // - 2 + 3 = 1
                    new Token(TokenType.OP_MINUS, "-"),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(1.0);
        }

        @Test
        void evaluateUnary() {
            var buffer = new TokenBuffer(List.of( // 5 - (-2 * 3) = 11
                    new Token(TokenType.NUMBER, "5"),
                    new Token(TokenType.OP_MINUS, "-"),
                    new Token(TokenType.LEFT_BRACKET, "("),
                    new Token(TokenType.OP_MINUS, "-"),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_MULTIPLICATION, "*"),
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.RIGHT_BRACKET, ")"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(11.0);
        }
    }

    @Nested
    @DisplayName("Функции")
    class Functions {
        @Test
        void minimalValueFunctions() {
            var buffer = new TokenBuffer(List.of( // 2 + min(3, 9) = 5
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.FUNCTION, "min"),
                    new Token(TokenType.LEFT_BRACKET, "("),
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.COMMA, ","),
                    new Token(TokenType.NUMBER, "9"),
                    new Token(TokenType.RIGHT_BRACKET, ")"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(5.0);
        }

        @Test
        void powValueFunctions() {
            var buffer = new TokenBuffer(List.of( // 2 + pow(3, 2) = 11
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.FUNCTION, "pow"),
                    new Token(TokenType.LEFT_BRACKET, "("),
                    new Token(TokenType.NUMBER, "3"),
                    new Token(TokenType.COMMA, ","),
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.RIGHT_BRACKET, ")"),
                    new Token(TokenType.EOF, "")
            ));

            var calculatedResult = syntaxEvaluator.calculateExpression(buffer);

            assertThat(calculatedResult)
                    .as("Неверный результат расчета")
                    .isEqualTo(11.0);
        }

        @Test
        void powValueFunctionsBrokenBracket() {
            var buffer = new TokenBuffer(List.of( // 2 + pow) = ошибка
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.FUNCTION, "pow"),
                    new Token(TokenType.RIGHT_BRACKET, ")"),
                    new Token(TokenType.EOF, "")
            ));

            assertThatThrownBy(() -> {
                syntaxEvaluator.calculateExpression(buffer);
            })
                    .as("Ожидался неверный токен")
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining("Unexpected token at position: 4");
        }

        @Test
        void powValueFunctionsUnexpectedToken() {
            var buffer = new TokenBuffer(List.of( // 2 + pow(%) = ошибка
                    new Token(TokenType.NUMBER, "2"),
                    new Token(TokenType.OP_PLUS, "+"),
                    new Token(TokenType.FUNCTION, "pow"),
                    new Token(TokenType.NUMBER, "%"),
                    new Token(TokenType.RIGHT_BRACKET, ")"),
                    new Token(TokenType.EOF, "")
            ));

            assertThatThrownBy(() -> {
                syntaxEvaluator.calculateExpression(buffer);
            })
                    .as("Ожидался неверный токен")
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining("Unexpected token at position: 4");
        }
    }

    @Nested
    class MinimalValueFunctions implements CalculateFunction {
        @Override
        public double apply(List<Double> args) {
            return args.stream()
                    .min(Double::compare)
                    .orElseThrow(() -> new ArithmeticException("Empty args list"));
        }
    }

    @Nested
    class PowValueFunctions implements CalculateFunction {
        @Override
        public double apply(List<Double> args) {
            return Math.pow(args.get(0), args.get(1));
        }
    }
}