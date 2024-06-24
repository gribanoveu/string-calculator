package com.github.gribanoveu.libruary.parser;


import com.github.gribanoveu.libruary.entity.Token;
import com.github.gribanoveu.libruary.entity.TokenType;
import com.github.gribanoveu.libruary.exception.FunctionException;
import com.github.gribanoveu.libruary.util.FunctionManager;

import java.util.ArrayList;
import java.util.List;

public class ExpressionTokenizer implements Tokenizer {
    private final FunctionManager functionManager;

    public ExpressionTokenizer(FunctionManager functionManager) {
        this.functionManager = functionManager;
    }

    private static final Token LEFT_BRACKET = new Token(TokenType.LEFT_BRACKET, "(");
    private static final Token RIGHT_BRACKET = new Token(TokenType.RIGHT_BRACKET, ")");
    private static final Token OP_PLUS = new Token(TokenType.OP_PLUS, "+");
    private static final Token OP_MINUS = new Token(TokenType.OP_MINUS, "-");
    private static final Token OP_MULTIPLICATION = new Token(TokenType.OP_MULTIPLICATION, "*");
    private static final Token OP_DIVISION = new Token(TokenType.OP_DIVISION, "/");
    private static final Token COMMA = new Token(TokenType.COMMA, ",");
    private static final Token EOF = new Token(TokenType.EOF, "");

    @Override
    public List<Token> parseExpression(String inputExpression) {
        var expression = inputExpression.replaceAll("\\s+", "");
        var tokens = new ArrayList<Token>();
        int index = 0;

        while (index < expression.length()) {
            var currentChar = expression.charAt(index);
            if (Character.isDigit(currentChar)) {
                var numberToken = readNumber(expression, index);
                tokens.add(numberToken);
                index += numberToken.value().length(); // изменить позицию после чтения всего номера
            } else if (Character.isAlphabetic(currentChar)) {
                var functionToken = readFunction(expression, index);
                tokens.add(functionToken);
                index += functionToken.value().length(); // изменить позицию после чтения имени функции
            } else {
                switch (currentChar) {
                    case '(' -> tokens.add(LEFT_BRACKET);
                    case ')' -> tokens.add(RIGHT_BRACKET);
                    case '+' -> tokens.add(OP_PLUS);
                    case '-' -> tokens.add(OP_MINUS);
                    case '*' -> tokens.add(OP_MULTIPLICATION);
                    case '/' -> tokens.add(OP_DIVISION);
                    case ',' -> tokens.add(COMMA);
                    default -> throw new ArithmeticException("Unexpected character: " + currentChar);
                }
                index++;
            }
        }
        tokens.add(EOF);
        return tokens;
    }

    /**
     * Собрать число, если оно состоит из больше, чем одного символа.
     * @param expression входящее выражение
     * @param start позиция токена
     * @return полное число
     */
    private Token readNumber(String expression, int start) {
        var digitBuilder = new StringBuilder();
        while (start < expression.length() && Character.isDigit(expression.charAt(start))) {
            digitBuilder.append(expression.charAt(start++));
        }
        return new Token(TokenType.NUMBER, digitBuilder.toString());
    }

    /**
     * Собрать имя функции и проверить, что она зарегистрирована (доступна для расчета)
     * @param expression входящее выражение
     * @param start позиция токена
     * @return имя функции
     */
    private Token readFunction(String expression, int start) {
        var functionNameBuilder = new StringBuilder();
        while (start < expression.length() && Character.isAlphabetic(expression.charAt(start))) {
            functionNameBuilder.append(expression.charAt(start++));
        }
        var functionName = functionNameBuilder.toString();
        if (functionManager.isFunctionExist(functionName)) {
            return new Token(TokenType.FUNCTION, functionName);
        } else {
            throw new FunctionException("Function not found: " + functionName);
        }
    }
}

