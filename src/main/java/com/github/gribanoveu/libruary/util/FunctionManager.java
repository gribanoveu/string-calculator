package com.github.gribanoveu.libruary.util;


import com.github.gribanoveu.libruary.function.CalculateFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Менеджер для управления всеми функциями.
 * @author Evgeny Gribanov
 * @version 16.04.2024
 */
public class FunctionManager {
    private final Map<String, CalculateFunction> functions;

    public FunctionManager() {
        functions = new HashMap<>();
    }

    /**
     * Зарегистрировать функцию.
     * @param name имя функции, по которому она будет доступна для расчетов.
     * @param function реализация функции.
     */
    public void registerFunction(String name, CalculateFunction function) {
        functions.put(name, function);
    }

    /**
     * Проверить, существует ли функция в зарегистрированных.
     * @param name имя функции, по которому она будет доступна для расчетов.
     * @return результат проверки.
     */
    public boolean isFunctionExist(String name) {
        return functions.containsKey(name);
    }

    /**
     * Применить функцию.
     * @param name имя функции, по которому она будет доступна для расчетов.
     * @param args список аргументов внутри нее.
     * @return результат вычисления функции.
     */
    public double applyFunction(String name, List<Double> args) {
        return functions.get(name).apply(args);
    }
}