package com.github.gribanoveu.libruary.calculator;

import com.github.gribanoveu.libruary.function.CalculateFunction;
import com.github.gribanoveu.libruary.util.FunctionManager;

/**
 * Инициализация калькулятора с помощью билдера с регистрацией функций.
 * @author Evgeny Gribanov
 * @version 28.05.2024
 */
public class ArithmeticCalculatorBuilder {
    private final FunctionManager functionManager;

    public ArithmeticCalculatorBuilder() {
        this.functionManager = new FunctionManager();
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
