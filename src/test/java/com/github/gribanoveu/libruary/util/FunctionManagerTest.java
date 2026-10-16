package com.github.gribanoveu.libruary.util;

import com.github.gribanoveu.libruary.exception.FunctionException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Evgeny Gribanov
 * @version 16.10.2026
 */
class FunctionManagerTest {
    private final FunctionManager functionManager = new FunctionManager();

    @Test
    void applyFunctionThrowsForUnknownName() {
        assertThatThrownBy(() -> functionManager.applyFunction("unknown", List.of(1.0)))
                .as("Неизвестная функция должна давать FunctionException, а не NullPointerException")
                .isInstanceOf(FunctionException.class)
                .hasMessageContaining("Function not found: unknown");
    }

    @Test
    void registeredFunctionIsAvailable() {
        functionManager.registerFunction("min", args -> args.stream().min(Double::compare).orElseThrow());

        assertThat(functionManager.isFunctionExist("min")).isTrue();
        assertThat(functionManager.applyFunction("min", List.of(3.0, 1.0))).isEqualTo(1.0);
    }
}
