package com.github.gribanoveu.libruary.mcp;

import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Evgeny Gribanov
 * @version 16.10.2026
 */
class CalculatorMcpServerTest {
    @Test
    void calculatesExpression() {
        var result = CalculatorMcpServer.calculate(null, new CallToolRequest("calculate",
                Map.of("expression", "122 + 3 -2* (2 * 5 + 2) * 4 + min(1, 2)")));
        assertThat(result.isError()).isFalse();
        assertThat(((TextContent) result.content().get(0)).text()).isEqualTo("30.0");
    }

    @Test
    void returnsErrorForInvalidExpression() {
        var result = CalculatorMcpServer.calculate(null, new CallToolRequest("calculate",
                Map.of("expression", "max(1, 5) * (2 + \"x\".length)")));
        assertThat(result.isError()).isTrue();
    }

    @Test
    void returnsErrorWithoutExpression() {
        var result = CalculatorMcpServer.calculate(null, new CallToolRequest("calculate", Map.of()));
        assertThat(result.isError()).isTrue();
        assertThat(((TextContent) result.content().get(0)).text()).contains("missing 'expression'");
    }

    @Test
    void returnsErrorForTooLongExpression() {
        var result = CalculatorMcpServer.calculate(null, new CallToolRequest("calculate",
                Map.of("expression", "1+".repeat(600) + "1")));
        assertThat(result.isError()).isTrue();
        assertThat(((TextContent) result.content().get(0)).text())
                .as("Слишком длинное выражение должно отклоняться до разбора")
                .contains("too long");
    }

    @Test
    void calculatesDeeplyNestedExpression() {
        var result = CalculatorMcpServer.calculate(null, new CallToolRequest("calculate",
                Map.of("expression", "(".repeat(400) + "1" + ")".repeat(400))));
        assertThat(result.isError()).isFalse();
        assertThat(((TextContent) result.content().get(0)).text()).isEqualTo("1.0");
    }
}
