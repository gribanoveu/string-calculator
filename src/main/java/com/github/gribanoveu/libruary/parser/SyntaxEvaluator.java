package com.github.gribanoveu.libruary.parser;



import com.github.gribanoveu.libruary.exception.InvalidTokenException;
import com.github.gribanoveu.libruary.util.FunctionManager;
import com.github.gribanoveu.libruary.util.TokenBuffer;

import java.util.ArrayList;

import static com.github.gribanoveu.libruary.entity.TokenType.*;


/**
 * Синтаксический анализатор.
 * <p>
 * Правила работы:
 * <code>
 * ВЫРАЖЕНИЕ:  ПлюсМинус*  EOF ;                             сложение или вычитанием и конец строки      6
 * ПлюсМинус:  УмножДелен  [('+' | '-') УмножДелен ]* ;      подвыражение со сложением или вычитанием    4 + 2
 * УмножДелен: Множит [('*' | '/') Множит ]* ;               подвыражение с умножением или делением      2 * 2
 * Множит:     Функц | Унарн | Число | '(' ВЫРАЖЕНИЕ ')' ;   номер или выражение в скобках               2 + (2 * 2)
 * Унарн:      ('-' | '+') Множит                            унарное выражение                           - 2 + 4
 * Функц:       Имя '(' ВЫРАЖЕНИЕ (, ВЫРАЖЕНИЕ)+)? ')'       функция                                     min(2, 4)
 * </code>
 * Вычисление идет снизу вверх.
 *
 *
 * @author Evgeny Gribanov
 * @version 18.04.2024
 */
public class SyntaxEvaluator implements ExpressionEvaluator {

    private final FunctionManager functionManager;

    public SyntaxEvaluator(FunctionManager functionManager) {
        this.functionManager = functionManager;
    }

    /**
     * Вычисляем полученное выражение.
     * Функция высшего порядка.
     * Правило:
     * ВЫРАЖЕНИЕ: ПлюсМинус* EOF ;
     * Вычисляет сложение или вычитанием и конец строки: 6.
     * Получим результат вычисления выражения.
     */
    @Override
    public double calculateExpression(TokenBuffer buffer) {
        if (buffer.next().type() == EOF) {
            return 0.0; // пустое выражение
        }
        buffer.back();
        var value = calculatePlusMinus(buffer);
        if (buffer.next().type() != EOF) { // например (2)(3): после выражения остались токены
            throw new InvalidTokenException(buffer);
        }
        return value;
    }


    /**
     * Вычисляем сложение и вычитание.
     * Правило:
     * ПлюсМинус: УмножДелен [('+' | '-') УмножДелен]* ;
     * Вычисляет подвыражение со сложением или вычитанием: 4 + 2.
     * Результатом будет полученное значение сложения или вычитания.
     */
    private double calculatePlusMinus(TokenBuffer buffer) {
        var value = calculateMultiplicationDivision(buffer);
        while (true) { // читаем все выражение
            var token = buffer.next();
            switch (token.type()) { // считаем только сложение и вычитание
                case OP_PLUS -> value += calculateMultiplicationDivision(buffer);
                case OP_MINUS -> value -= calculateMultiplicationDivision(buffer);
                default -> { // если встретилось что-то другое, значит выражение закончилось
                    buffer.back(); // откатываемся на токен назад
                    return value;
                }
            }
        }
    }

    /**
     * Вычисляем умножение и деление.
     * Правило:
     * УмножДелен: Множит [('*' | '/') Множит]* ;
     * Вычисляет подвыражение с умножением или делением: 2 * 2.
     * Результатом будет полученное значение умножения или деления.
     * Выражение в скобках уже будет посчитано.
     */
    private double calculateMultiplicationDivision(TokenBuffer buffer) {
        var value = calculateBrackets(buffer);
        while (true) { // читаем все выражение
            var token = buffer.next();
            switch (token.type()) { // считаем только умножение и деление
                case OP_MULTIPLICATION -> value *= calculateBrackets(buffer);
                case OP_DIVISION -> {
                    var divisor = calculateBrackets(buffer);
                    if (divisor == 0) { // иначе в ответ уйдет Infinity или NaN
                        throw new ArithmeticException("Division by zero");
                    }
                    value /= divisor;
                }
                default -> { // если встретилось что-то другое, значит выражение закончилось
                    buffer.back(); // откатываемся на токен назад
                    return value;
                }
            }
        }
    }

    /**
     * Вычисляет выражение в скобках.
     * Функция низшего порядка.
     * Правило:
     * Множит: Унарн | Число | '(' ВЫРАЖЕНИЕ ')' ;
     * Вычисляет значение числа или выражение в скобках: 2 + (2 * 2).
     * Результатом вернет для первого токена: 2, для второго токена: 4.
     */
    private double calculateBrackets(TokenBuffer buffer) {
        var token = buffer.next();
        switch (token.type()) {
            case FUNCTION -> {
                buffer.back();
                return calculateFunction(buffer);
            }
            case OP_MINUS -> { // если унарный минус, функция вызывает саму себя
                var value = calculateBrackets(buffer);
                return -value; // возвращает значение со знаком минус
            }
            case OP_PLUS -> { // унарный плюс: +5 и 2 * +5
                return calculateBrackets(buffer);
            }
            case NUMBER -> { // если токен число, то возвращаем число
                return Double.parseDouble(token.value());
            }
            case LEFT_BRACKET -> { // если токен открывающая скобка
                // внутри скобок выражение заканчивается на закрывающую скобку, а не на EOF,
                // поэтому здесь нельзя звать calculateExpression
                var value = calculatePlusMinus(buffer);
                token = buffer.next();
                if (token.type() != RIGHT_BRACKET) { // если нет закрывающей скобки, значит неверное выражение
                    throw new InvalidTokenException(buffer);
                }
                return value;
            } // иначе синтаксис неверный
            default -> throw new InvalidTokenException(buffer);
        }
    }

    /**
     * Вычисляет выражение в функции.
     * Правило:
     * Функц: Имя '(' ВЫРАЖЕНИЕ (, ВЫРАЖЕНИЕ)+)? ')'
     * Вычисляет все что находится внутри выражения функции: min(2, 4).
     * Результатом будет полученное значение в скобках.
     */
    private double calculateFunction(TokenBuffer buffer) {
        var name = buffer.next().value();
        var token = buffer.next();
        if (token.type() != LEFT_BRACKET) { // начать с открывающей скобки
            throw new InvalidTokenException(buffer);
        }

        var args = new ArrayList<Double>();
        token = buffer.next(); // если сразу ')', то аргументов нет
        if (token.type() != RIGHT_BRACKET) {
            buffer.back();
            do { // вычислить выражение внутри
                args.add(calculatePlusMinus(buffer)); // аргумент заканчивается на ',' или ')'
                token = buffer.next();

                if (token.type() != COMMA && token.type() != RIGHT_BRACKET) {
                    throw new InvalidTokenException(buffer);
                }

            } while (token.type() == COMMA);
        }
        return functionManager.applyFunction(name, args);
    }
}
