package com.github.gribanoveu.libruary.function;

import java.util.List;

/**
 * Интерфейс, который должна реализовать функция для выполнения дополнительной логики расчетов.
 * Пример функции - поиск минимального числа - {@link MinimalValueFunction}
 * @author Evgeny Gribanov
 * @version 17.04.2024
 */
@FunctionalInterface
public interface CalculateFunction {
    double apply(List<Double> args);
}
