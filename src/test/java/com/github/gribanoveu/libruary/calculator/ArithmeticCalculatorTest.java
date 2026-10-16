package com.github.gribanoveu.libruary.calculator;

import com.github.gribanoveu.libruary.exception.FunctionException;
import com.github.gribanoveu.libruary.exception.InvalidTokenException;
import com.github.gribanoveu.libruary.function.CalculateFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Evgeny Gribanov
 * @version 20.04.2024
 */
class ArithmeticCalculatorTest {
    private Calculator calculator;
    private final Calculator defaultCalculator = new ArithmeticCalculatorBuilder().build();

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

    @Test
    void calculatesWithDefaultFunctions() {
        var expressions = Map.ofEntries(
                Map.entry("min(1, 2)", 1.0),
                Map.entry("max(1, 2)", 2.0),
                Map.entry("sum(1, 2, 3)", 6.0),
                Map.entry("avg(1, 2, 3)", 2.0),
                Map.entry("abs(-5)", 5.0),
                Map.entry("sqrt(16)", 4.0),
                Map.entry("pow(2, 10)", 1024.0),
                Map.entry("mod(7, 3)", 1.0),
                Map.entry("round(2.6)", 3.0),
                Map.entry("floor(1.8)", 1.0),
                Map.entry("ceil(1.2)", 2.0),
                Map.entry("ln(1)", 0.0),
                Map.entry("log(100)", 2.0),
                Map.entry("exp(0)", 1.0),
                Map.entry("sin(0)", 0.0),
                Map.entry("cos(0)", 1.0),
                Map.entry("tan(0)", 0.0));

        assertThat(expressions).allSatisfy((expression, expected) ->
                assertThat(defaultCalculator.calculate(expression)).as(expression).isEqualTo(expected));
    }

    @Test
    void defaultFunctionsCheckArgumentCount() {
        assertThatThrownBy(() -> defaultCalculator.calculate("sqrt()"))
                .as("Функция без аргумента должна называть ожидаемое количество")
                .isInstanceOf(FunctionException.class)
                .hasMessageContaining("sqrt: expected 1 argument(s), got 0");
        assertThatThrownBy(() -> defaultCalculator.calculate("sqrt(1, 2)"))
                .isInstanceOf(FunctionException.class)
                .hasMessageContaining("sqrt: expected 1 argument(s), got 2");
        assertThatThrownBy(() -> defaultCalculator.calculate("pow(2)"))
                .isInstanceOf(FunctionException.class)
                .hasMessageContaining("pow: expected 2 argument(s), got 1");
        assertThatThrownBy(() -> defaultCalculator.calculate("sum()"))
                .isInstanceOf(FunctionException.class)
                .hasMessageContaining("sum: expected at least 1 argument");
    }

    @Test
    void defaultFunctionsRejectUndefinedResult() {
        assertThatThrownBy(() -> defaultCalculator.calculate("sqrt(-1)"))
                .as("Неопределённый результат не должен уходить в ответ как NaN")
                .isInstanceOf(FunctionException.class)
                .hasMessageContaining("sqrt: result is undefined");
        assertThatThrownBy(() -> defaultCalculator.calculate("mod(7, 0)"))
                .isInstanceOf(FunctionException.class)
                .hasMessageContaining("mod: division by zero");
    }

    @Test
    void customFunctionReplacesDefault() {
        var customCalculator = new ArithmeticCalculatorBuilder()
                .registerFunction("sqrt", args -> 42.0)
                .build();

        assertThat(customCalculator.calculate("sqrt(16)"))
                .as("Своя функция с тем же именем должна заменять стандартную")
                .isEqualTo(42.0);
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