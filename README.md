#### ENG

A java based calculator with support for custom functions.

You should use builder to initialize the calculator.

    var calculator = new ArithmeticCalculatorBuilder().build();
    var resultCalculation = calculator.calculate("- 4 + 8 + (3 +2-1)");

The builder registers these functions by default: `min`, `max`, `sum`, `avg`, `abs`, `sqrt`, `pow`,
`mod`, `round`, `floor`, `ceil`, `ln` (natural logarithm), `log` (base 10), `exp`, `sin`, `cos`, `tan`
(radians). A function of your own with the same name replaces the standard one.

To add your own function, you must implement the CalculateFunction interface and register the function when the calculator is initialized.

    var calculator = new ArithmeticCalculatorBuilder()
        .registerFunction("max", new MaximalValueCustomFunction()))
        .build();

    var resultFunction = calculator.calculate("max(4, 8)");

If you call a function that is not registered, a FunctionException exception will occur.
When adding your own functions, you should also use this exception.

##### MCP server

The jar can be run as an MCP server (stdio transport) with a single `calculate` tool
(all standard functions listed above are available). The protocol is handled by the official MCP Java SDK
(`io.modelcontextprotocol.sdk:mcp`), so `mvn package` builds a self-contained jar (~6 MB) with all
dependencies; the JVM flags below reduce memory usage (~67 MB RSS) and startup time.

    mvn package

    {
      "mcpServers": {
        "calculator": {
          "command": "java",
          "args": ["-XX:TieredStopAtLevel=1", "-XX:+UseSerialGC", "-Xmx16m", "-Xss512k", "-XX:-UsePerfData",
                   "-jar", "/path/to/string-calculator-1.0.0.jar"]
        }
      }
    }

For Claude Code: `claude mcp add calculator -- java -XX:TieredStopAtLevel=1 -XX:+UseSerialGC -Xmx16m -jar /path/to/string-calculator-1.0.0.jar`

#### RUS

Калькулятор на основе java с поддержкой пользовательских функций.

Для инициализации калькулятора следует использовать билдер.

    var calculator = new ArithmeticCalculatorBuilder().build();
    var resultCalculation = calculator.calculate("- 4 + 8 + (3 +2-1)");

Билдер регистрирует стандартные функции: `min`, `max`, `sum`, `avg`, `abs`, `sqrt`, `pow`, `mod`,
`round`, `floor`, `ceil`, `ln` (натуральный логарифм), `log` (по основанию 10), `exp`, `sin`, `cos`,
`tan` (в радианах). Своя функция с тем же именем заменит стандартную.

Чтобы добавить собственную функцию, необходимо реализовать интерфейс CalculateFunction и зарегистрировать функцию при инициализации калькулятора.

    var calculator = new ArithmeticCalculatorBuilder()
        .registerFunction("max", new MaximalValueCustomFunction())
        .build();

    var resultFunction = calculator.calculate("max(4, 8)");

Если вы вызовете функцию, которая не зарегистрирована, возникнет исключение FunctionException.
При добавлении собственных функций также следует использовать это исключение.

##### MCP сервер

Jar можно запустить как MCP сервер (транспорт stdio) с одним инструментом `calculate`
(доступны все стандартные функции, перечисленные выше). Протокол обрабатывает официальный MCP Java SDK
(`io.modelcontextprotocol.sdk:mcp`), поэтому `mvn package` собирает self-contained jar (~6 МБ) со всеми
зависимостями, а флаги JVM ниже уменьшают потребление памяти (~67 МБ RSS) и время старта.

    mvn package

    {
      "mcpServers": {
        "calculator": {
          "command": "java",
          "args": ["-XX:TieredStopAtLevel=1", "-XX:+UseSerialGC", "-Xmx16m", "-Xss512k", "-XX:-UsePerfData",
                   "-jar", "/path/to/string-calculator-1.0.0.jar"]
        }
      }
    }

Для Claude Code: `claude mcp add calculator -- java -XX:TieredStopAtLevel=1 -XX:+UseSerialGC -Xmx16m -jar /path/to/string-calculator-1.0.0.jar`
