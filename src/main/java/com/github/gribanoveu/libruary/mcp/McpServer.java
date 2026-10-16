package com.github.gribanoveu.libruary.mcp;

import com.github.gribanoveu.libruary.calculator.ArithmeticCalculatorBuilder;
import com.github.gribanoveu.libruary.calculator.Calculator;
import com.github.gribanoveu.libruary.function.MaximalValueFunction;
import com.github.gribanoveu.libruary.function.MinimalValueFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * MCP сервер (транспорт stdio, JSON-RPC 2.0) с одним инструментом calculate.
 * Реализован без внешних зависимостей, чтобы минимизировать потребление памяти и время старта.
 * @author Evgeny Gribanov
 * @version 14.10.2026
 */
public final class McpServer {
    private static final String TOOLS = "{\"tools\":[{\"name\":\"calculate\","
            + "\"description\":\"Evaluates an arithmetic expression with + - * / ( ), unary minus and functions min(a, b, ...), max(a, b, ...)\","
            + "\"inputSchema\":{\"type\":\"object\",\"properties\":{\"expression\":{\"type\":\"string\","
            + "\"description\":\"Expression, e.g. 2 * (3 + 4) - max(1, 5)\"}},\"required\":[\"expression\"]}}]}";

    private final Calculator calculator = new ArithmeticCalculatorBuilder()
            .registerFunction("min", new MinimalValueFunction())
            .registerFunction("max", new MaximalValueFunction())
            .build();

    public static void main(String[] args) throws IOException {
        var server = new McpServer();
        var in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        for (String line; (line = in.readLine()) != null; ) {
            var response = line.isBlank() ? null : server.handle(line);
            if (response != null) {
                System.out.println(response);
                System.out.flush();
            }
        }
    }

    /**
     * Обрабатывает одно JSON-RPC сообщение.
     * @return ответ или null, если сообщение является уведомлением
     */
    String handle(String message) {
        Map<?, ?> request;
        try {
            request = (Map<?, ?>) new Json(message).value();
        } catch (RuntimeException e) {
            return error("null", -32700, "Parse error");
        }
        var id = request.get("id");
        if (id == null) {
            return null;
        }
        var jsonId = id instanceof String s ? quote(s) : id.toString();
        var method = String.valueOf(request.get("method"));
        var params = request.get("params") instanceof Map<?, ?> p ? p : Map.of();
        var result = switch (method) {
            case "initialize" -> "{\"protocolVersion\":"
                    + quote(params.get("protocolVersion") instanceof String v ? v : "2025-06-18")
                    + ",\"capabilities\":{\"tools\":{}},\"serverInfo\":{\"name\":\"string-calculator\",\"version\":\"1.0.0\"}}";
            case "ping" -> "{}";
            case "tools/list" -> TOOLS;
            case "tools/call" -> call(params);
            default -> null;
        };
        return result == null
                ? error(jsonId, -32601, "Method not found: " + method)
                : "{\"jsonrpc\":\"2.0\",\"id\":" + jsonId + ",\"result\":" + result + "}";
    }

    private String call(Map<?, ?> params) {
        var expression = params.get("arguments") instanceof Map<?, ?> a ? a.get("expression") : null;
        if (!"calculate".equals(params.get("name")) || !(expression instanceof String e)) {
            return toolResult("Unknown tool or missing 'expression' argument", true);
        }
        try {
            return toolResult(String.valueOf(calculator.calculate(e)), false);
        } catch (RuntimeException ex) {
            return toolResult(ex.getMessage() != null ? ex.getMessage() : ex.toString(), true);
        }
    }

    private static String toolResult(String text, boolean isError) {
        return "{\"content\":[{\"type\":\"text\",\"text\":" + quote(text) + "}],\"isError\":" + isError + "}";
    }

    private static String error(String jsonId, int code, String message) {
        return "{\"jsonrpc\":\"2.0\",\"id\":" + jsonId + ",\"error\":{\"code\":" + code + ",\"message\":" + quote(message) + "}}";
    }

    private static String quote(String value) {
        var sb = new StringBuilder("\"");
        for (var c : value.toCharArray()) {
            if (c == '"' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c < 0x20 || c > 0x7e) {
                sb.append("\\u").append(Integer.toHexString(0x10000 | c), 1, 5);
            } else {
                sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    /**
     * Минимальный JSON парсер: объекты -> Map, массивы -> List, числа -> BigDecimal.
     */
    private static final class Json {
        private final String s;
        private int i;

        Json(String s) {
            this.s = s;
        }

        Object value() {
            skipWhitespace();
            var c = s.charAt(i);
            if (c == '"') {
                return string();
            }
            if (c == '{' || c == '[') {
                var map = new HashMap<String, Object>();
                var list = new ArrayList<>();
                var end = c == '{' ? '}' : ']';
                i++;
                skipWhitespace();
                if (s.charAt(i) == end) {
                    i++;
                } else do {
                    if (c == '{') {
                        skipWhitespace();
                        var key = string();
                        skipWhitespace();
                        if (s.charAt(i++) != ':') throw new IllegalArgumentException("Expected ':'");
                        map.put(key, value());
                    } else {
                        list.add(value());
                    }
                    skipWhitespace();
                } while (s.charAt(i++) == ',');
                if (s.charAt(i - 1) != end) throw new IllegalArgumentException("Expected " + end);
                return c == '{' ? map : list;
            }
            var start = i;
            while (i < s.length() && ",}] \t\r\n".indexOf(s.charAt(i)) < 0) i++;
            var literal = s.substring(start, i);
            return switch (literal) {
                case "true" -> Boolean.TRUE;
                case "false" -> Boolean.FALSE;
                case "null" -> null;
                default -> new BigDecimal(literal);
            };
        }

        private String string() {
            if (s.charAt(i++) != '"') throw new IllegalArgumentException("Expected string");
            var sb = new StringBuilder();
            for (char c; (c = s.charAt(i++)) != '"'; ) {
                if (c == '\\') {
                    c = s.charAt(i++);
                    switch (c) {
                        case 'b' -> c = '\b';
                        case 'f' -> c = '\f';
                        case 'n' -> c = '\n';
                        case 'r' -> c = '\r';
                        case 't' -> c = '\t';
                        case 'u' -> {
                            c = (char) Integer.parseInt(s, i, i + 4, 16);
                            i += 4;
                        }
                        default -> { }
                    }
                }
                sb.append(c);
            }
            return sb.toString();
        }

        private void skipWhitespace() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        }
    }
}
