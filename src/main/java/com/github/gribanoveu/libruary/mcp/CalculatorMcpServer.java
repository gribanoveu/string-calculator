package com.github.gribanoveu.libruary.mcp;

import com.github.gribanoveu.libruary.calculator.ArithmeticCalculatorBuilder;
import com.github.gribanoveu.libruary.calculator.Calculator;
import com.github.gribanoveu.libruary.function.MaximalValueFunction;
import com.github.gribanoveu.libruary.function.MinimalValueFunction;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;

import java.util.List;
import java.util.Map;

/**
 * MCP сервер (транспорт stdio) с одним инструментом calculate.
 * Протокол, JSON-RPC и разбор JSON выполняет MCP Java SDK.
 * @author Evgeny Gribanov
 * @version 16.10.2026
 */
public final class CalculatorMcpServer {
    private static final String TOOL_DESCRIPTION = "Evaluates an arithmetic expression with + - * / ( ), "
            + "unary minus and functions min(a, b, ...), max(a, b, ...)";

    private static final Map<String, Object> INPUT_SCHEMA = Map.of(
            "type", "object",
            "properties", Map.of("expression", Map.of(
                    "type", "string",
                    "description", "Expression, e.g. 2 * (3 + 4) - max(1, 5)")),
            "required", List.of("expression"));

    private static final Calculator CALCULATOR = new ArithmeticCalculatorBuilder()
            .registerFunction("min", new MinimalValueFunction())
            .registerFunction("max", new MaximalValueFunction())
            .build();

    /**
     * Предел длины выражения: разбор рекурсивный, и на глубине в тысячи скобок стек кончается,
     * а StackOverflowError не ловится как RuntimeException.
     */
    private static final int MAX_EXPRESSION_LENGTH = 1000;

    private CalculatorMcpServer() {
    }

    public static void main(String[] args) {
        var transport = new StdioServerTransportProvider(McpJsonDefaults.getMapper());
        McpServer.sync(transport)
                .serverInfo("string-calculator", "1.0.0")
                .capabilities(ServerCapabilities.builder().tools(false).build())
                .toolCall(Tool.builder("calculate", INPUT_SCHEMA).description(TOOL_DESCRIPTION).build(),
                        CalculatorMcpServer::calculate)
                .build();
    }

    /**
     * Обработчик вызова инструмента calculate.
     * @param exchange контекст сессии, обработчику не нужен
     * @param request аргументы вызова, ожидается expression
     * @return результат расчета или текст ошибки
     */
    static CallToolResult calculate(McpSyncServerExchange exchange, CallToolRequest request) {
        var expression = request.arguments() == null ? null : request.arguments().get("expression");
        if (!(expression instanceof String e)) {
            return result("Unknown tool or missing 'expression' argument", true);
        }
        if (e.length() > MAX_EXPRESSION_LENGTH) {
            return result("Expression is too long: %s characters, the limit is %s"
                    .formatted(e.length(), MAX_EXPRESSION_LENGTH), true);
        }
        try {
            return result(String.valueOf(CALCULATOR.calculate(e)), false);
        } catch (StackOverflowError error) { // Error, а не RuntimeException, поэтому отдельно
            return result("Expression is nested too deeply", true);
        } catch (RuntimeException ex) {
            return result(ex.getMessage() != null ? ex.getMessage() : ex.toString(), true);
        }
    }

    private static CallToolResult result(String text, boolean isError) {
        return CallToolResult.builder().content(List.of(new TextContent(text))).isError(isError).build();
    }
}
