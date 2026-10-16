package com.github.gribanoveu.libruary.function;

import com.github.gribanoveu.libruary.exception.FunctionException;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * Функции, которые калькулятор регистрирует по умолчанию.
 * <p>
 * Своя функция с тем же именем заменит стандартную. Углы тригонометрических функций — в радианах.
 * @author Evgeny Gribanov
 * @version 16.10.2026
 */
public final class DefaultFunctions {
    private static final Map<String, CalculateFunction> FUNCTIONS = Map.ofEntries(
            Map.entry("min", new MinimalValueFunction()),
            Map.entry("max", new MaximalValueFunction()),
            Map.entry("sum", args -> {
                requireNotEmpty("sum", args);
                return args.stream().mapToDouble(Double::doubleValue).sum();
            }),
            Map.entry("avg", args -> {
                requireNotEmpty("avg", args);
                return args.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
            }),
            Map.entry("abs", unary("abs", Math::abs)),
            Map.entry("sqrt", unary("sqrt", Math::sqrt)),
            Map.entry("pow", binary("pow", Math::pow)),
            Map.entry("mod", binary("mod", (dividend, divisor) -> {
                if (divisor == 0) { // иначе вернется NaN
                    throw new FunctionException("mod: division by zero");
                }
                return dividend % divisor;
            })),
            Map.entry("round", unary("round", value -> Math.round(value))),
            Map.entry("floor", unary("floor", Math::floor)),
            Map.entry("ceil", unary("ceil", Math::ceil)),
            Map.entry("ln", unary("ln", Math::log)),
            Map.entry("log", unary("log", Math::log10)),
            Map.entry("exp", unary("exp", Math::exp)),
            Map.entry("sin", unary("sin", Math::sin)),
            Map.entry("cos", unary("cos", Math::cos)),
            Map.entry("tan", unary("tan", Math::tan))
    );

    private DefaultFunctions() {
    }

    /**
     * Стандартные функции: имя -> реализация.
     * @return неизменяемая карта функций.
     */
    public static Map<String, CalculateFunction> all() {
        return FUNCTIONS;
    }

    private static CalculateFunction unary(String name, DoubleUnaryOperator operator) {
        return args -> {
            requireArguments(name, args, 1);
            return definedResult(name, operator.applyAsDouble(args.get(0)));
        };
    }

    private static CalculateFunction binary(String name, DoubleBinaryOperator operator) {
        return args -> {
            requireArguments(name, args, 2);
            return definedResult(name, operator.applyAsDouble(args.get(0), args.get(1)));
        };
    }

    private static void requireArguments(String name, List<Double> args, int expected) {
        if (args.size() != expected) {
            throw new FunctionException("%s: expected %s argument(s), got %s"
                    .formatted(name, expected, args.size()));
        }
    }

    private static void requireNotEmpty(String name, List<Double> args) {
        if (args.isEmpty()) {
            throw new FunctionException(name + ": expected at least 1 argument, got 0");
        }
    }

    /**
     * Проверить, что результат определен: NaN и бесконечность в ответ уходить не должны.
     */
    private static double definedResult(String name, double result) {
        if (!Double.isFinite(result)) {
            throw new FunctionException(name + ": result is undefined for the given arguments");
        }
        return result;
    }
}
