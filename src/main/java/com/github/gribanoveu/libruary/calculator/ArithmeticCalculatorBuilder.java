package com.github.gribanoveu.libruary.calculator;

import com.github.gribanoveu.libruary.function.CalculateFunction;
import com.github.gribanoveu.libruary.function.DefaultFunctions;
import com.github.gribanoveu.libruary.util.FunctionManager;

/**
 * Инициализация калькулятора с помощью билдера с регистрацией функций.
 * @author Evgeny Gribanov
 * @version 28.05.2024
 */
public class ArithmeticCalculatorBuilder {
    private final FunctionManager functionManager;

    /**
     * Создать билдер со стандартными функциями, которые уже зарегистрированы:
     * min, max, sum, avg, abs, sqrt, pow, mod, round, floor, ceil, ln, log, exp, sin, cos, tan.
     * Своя функция с тем же именем заменит стандартную.
     */
    public ArithmeticCalculatorBuilder() {
        this.functionManager = new FunctionManager();
        DefaultFunctions.all().forEach(functionManager::registerFunction);
    }

    /**
     * Создание объекта калькулятора.
     * Калькулятор создается следующим образом:
     * <pre>
     * var calculator = new ArithmeticCalculatorBuilder()
     *     .registerFunction("max", new MaximalValueCustomFunction())
     *     .build();
     * }
     * </pre>
     * При вызове функции, которая не зарегистрирована, возникнет исключение FunctionException.
     * При добавлении своих функций следует так же использовать это исключение при
     * возникновении ошибки в логике работы.
     * @return объект ArithmeticCalculator для дальнейшего добавления функций или вычислений
     */
    public ArithmeticCalculatorBuilder registerFunction(String name, CalculateFunction function) {
        functionManager.registerFunction(name, function);
        return this;
    }

    /**
     * Вызывает дальнейшие функции калькулятора с уже пред-настроенными функциями
     */
    public ArithmeticCalculator build() {
        return new ArithmeticCalculator(functionManager);
    }
}
