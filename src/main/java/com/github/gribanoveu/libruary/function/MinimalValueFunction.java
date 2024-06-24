package com.github.gribanoveu.libruary.function;

import java.util.List;

/**
 * Функция для поиска минимального значения.
 * Для использования необходимо зарегистрировать ее при инициализации и вызывать как min(1, 2, 3).
 * @author Evgeny Gribanov
 * @version 24.06.2024
 */
public class MinimalValueFunction implements CalculateFunction {
    @Override
    public double apply(List<Double> args) {
        return args.stream()
                .min(Double::compare)
                .orElseThrow(() -> new ArithmeticException("Empty args list"));
    }
}
