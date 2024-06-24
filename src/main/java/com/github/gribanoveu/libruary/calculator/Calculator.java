package com.github.gribanoveu.libruary.calculator;

/**
 * Интерфейс для калькулятора.
 * Реализует возможно высчитать выражение из строки и добавить свою функцию для расчета.
 * @author Evgeny Gribanov
 * @version 18.04.2024
 */
public interface Calculator {
    double calculate(String expression);
}
