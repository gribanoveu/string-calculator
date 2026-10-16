package com.github.gribanoveu.libruary.calculator;

import com.github.gribanoveu.libruary.exception.InvalidTokenException;
import com.github.gribanoveu.libruary.function.CalculateFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void calculateThrowsOnTrailingTokens() {
        assertThatThrownBy(() -> calculator.calculate("(2)(3)"))
                .as("Лишние токены после выражения должны приводить к ошибке")
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void calculateThrowsOnUnclosedBracket() {
        assertThatThrownBy(() -> calculator.calculate("2+("))
                .as("Незакрытая скобка должна давать ошибку разбора, а не выход за границы списка")
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void calculateFractionalNumbers() {
        assertThat(calculator.calculate("1.5 + 0.25"))
                .as("Числа с дробной частью должны поддерживаться")
                .isEqualTo(1.75);
    }

    @Test
    void calculateThrowsOnDivisionByZero() {
        assertThatThrownBy(() -> calculator.calculate("min(1, 1) / (2 - 2)"))
                .as("Деление на ноль должно быть ошибкой, а не Infinity")
                .isInstanceOf(ArithmeticException.class)
                .hasMessageContaining("Division by zero");
    }

    @Test
    void calculateThrowsOnFunctionWithoutArguments() {
        assertThatThrownBy(() -> calculator.calculate("min()"))
                .as("Функция без аргументов должна сообщать о пустом списке аргументов")
                .isInstanceOf(ArithmeticException.class)
                .hasMessageContaining("Empty args list");
    }

    @Test
    void calculateReportsCharacterPosition() {
        assertThatThrownBy(() -> calculator.calculate("1.5+"))
                .as("В сообщении об ошибке должна быть позиция в символах")
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("(character 4)");
    }

    @Test
    void calculateUnaryPlus() {
        assertThat(calculator.calculate("+5"))
                .as("Унарный плюс должен поддерживаться, как и унарный минус")
                .isEqualTo(5.0);
        assertThat(calculator.calculate("2 * +5")).isEqualTo(10.0);
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