package com.github.gribanoveu.libruary.calculator;

import com.github.gribanoveu.libruary.function.CalculateFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Evgeny Gribanov
 * @version 20.04.2024
 */
class ArithmeticCalculatorTest {
    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ArithmeticCalculatorBuilder()
                .registerFunction("min", new MinimalValueFunction())
                .registerFunction("pow", new PowValueFunction())
                .build();
    }

    @Test
    void calculateExpression() {
        var expression = "122 + 3 -2* (2 * 5 + 2) * 4";
        var calculatedResult = calculator.calculate(expression);
        assertThat(calculatedResult)
                .as("Результат выражения посчитан неверно")
                .isEqualTo(29.0);
    }

    @Test
    void calculateMinFunctionAdded() {
        var expression = "min(3 + 2 * 5, 9)";
        var calculatedResult = calculator.calculate(expression);
        assertThat(calculatedResult)
                .as("")
                .isEqualTo(9.0);
    }

    @Test
    void calculateEmptyString() {
        var expression = "";
        var calculatedResult = calculator.calculate(expression);
        assertThat(calculatedResult)
                .as("Пустая строка должна вернуть 0.0")
                .isEqualTo(0.0);
    }

    @Test
    void calculatePowFunctionAdded() {
        var expression = "pow(3, 2)";
        var calculatedResult = calculator.calculate(expression);
        assertThat(calculatedResult)
                .as("")
                .isEqualTo(9.0);
    }

    @Nested
    class MinimalValueFunction implements CalculateFunction {
        @Override
        public double apply(List<Double> args) {
            return args.stream()
                    .min(Double::compare)
                    .orElseThrow(() -> new ArithmeticException("Empty args list"));
        }
    }

    @Nested
    class PowValueFunction implements CalculateFunction {
        @Override
        public double apply(List<Double> args) {
            return Math.pow(args.get(0), args.get(1));
        }
    }
}