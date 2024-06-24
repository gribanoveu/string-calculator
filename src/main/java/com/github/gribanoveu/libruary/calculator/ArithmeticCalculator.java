package com.github.gribanoveu.libruary.calculator;

import com.github.gribanoveu.libruary.parser.ExpressionTokenizer;
import com.github.gribanoveu.libruary.parser.SyntaxEvaluator;
import com.github.gribanoveu.libruary.util.FunctionManager;
import com.github.gribanoveu.libruary.util.TokenBuffer;

/**
 * Арифметический калькулятор.
 * @author Evgeny Gribanov
 * @version 17.04.2024
 */
public class ArithmeticCalculator implements Calculator {
    private final FunctionManager functionManager;

    public ArithmeticCalculator(FunctionManager functionManager) {
        this.functionManager = functionManager;
    }

    /**
     * Вычисляет арифметическое выражение только с операторами + - * / ().
     * Дополнительные функции должны быть зарегистрированы перед использованием с помощью билдера.
     * @param expression строка с выражением
     * @return результат вычислений
     */
    @Override
    public double calculate(String expression) {
        var tokens = new ExpressionTokenizer(functionManager).parseExpression(expression);
        return new SyntaxEvaluator(functionManager).calculateExpression(new TokenBuffer(tokens));
    }
}
