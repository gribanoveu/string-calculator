package com.github.gribanoveu.libruary.mcp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Evgeny Gribanov
 * @version 14.10.2026
 */
class McpServerTest {
    private final McpServer server = new McpServer();

    @Test
    void initialize() {
        var response = server.handle("{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\","
                + "\"params\":{\"protocolVersion\":\"2025-03-26\",\"capabilities\":{},\"clientInfo\":{\"name\":\"t\",\"version\":\"1\"}}}");
        assertThat(response)
                .startsWith("{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":{\"protocolVersion\":\"2025-03-26\"")
                .contains("\"tools\":{}");
    }

    @Test
    void notificationHasNoResponse() {
        assertThat(server.handle("{\"jsonrpc\":\"2.0\",\"method\":\"notifications/initialized\"}")).isNull();
    }

    @Test
    void toolsList() {
        assertThat(server.handle("{\"jsonrpc\":\"2.0\",\"id\":\"a\",\"method\":\"tools/list\"}"))
                .startsWith("{\"jsonrpc\":\"2.0\",\"id\":\"a\",\"result\":{\"tools\":[{\"name\":\"calculate\"");
    }

    @Test
    void toolsCall() {
        var response = server.handle("{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"tools/call\","
                + "\"params\":{\"name\":\"calculate\",\"arguments\":{\"expression\":\"max(1, 5) * (2 + \\\"x\\\".length)\"}}}");
        assertThat(response).contains("\"isError\":true");

        response = server.handle("{\"jsonrpc\":\"2.0\",\"id\":3,\"method\":\"tools/call\","
                + "\"params\":{\"name\":\"calculate\",\"arguments\":{\"expression\":\"122 + 3 -2* (2 * 5 + 2) * 4 + min(1, 2)\"}}}");
        assertThat(response).isEqualTo("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":"
                + "{\"content\":[{\"type\":\"text\",\"text\":\"30.0\"}],\"isError\":false}}");
    }

    @Test
    void unknownMethodAndParseError() {
        assertThat(server.handle("{\"jsonrpc\":\"2.0\",\"id\":4,\"method\":\"foo\"}")).contains("\"code\":-32601");
        assertThat(server.handle("{oops")).contains("\"code\":-32700");
    }
}
