#### ENG

A java based calculator with support for custom functions.

You should use builder to initialize the calculator.

    var calculator = new ArithmeticCalculatorBuilder().build();
    var resultCalculation = calculator.calculate("- 4 + 8 + (3 +2-1)");

To add your own function, you must implement the CalculateFunction interface and register the function when the calculator is initialized.

    var calculator = new ArithmeticCalculatorBuilder()
        .registerFunction("max", new MaximalValueCustomFunction()))
        .build();

    var resultFunction = calculator.calculate("max(4, 8)");

If you call a function that is not registered, a FunctionException exception will occur.
When adding your own functions, you should also use this exception.

#### RUS

Калькулятор на основе java с поддержкой пользовательских функций.

Для инициализации калькулятора следует использовать билдер.

    var calculator = new ArithmeticCalculatorBuilder().build();
    var resultCalculation = calculator.calculate("- 4 + 8 + (3 +2-1)");

Чтобы добавить собственную функцию, необходимо реализовать интерфейс CalculateFunction и зарегистрировать функцию при инициализации калькулятора.

    var calculator = new ArithmeticCalculatorBuilder()
        .registerFunction("max", new MaximalValueCustomFunction())
        .build();

    var resultFunction = calculator.calculate("max(4, 8)");

Если вы вызовете функцию, которая не зарегистрирована, возникнет исключение FunctionException.
При добавлении собственных функций также следует использовать это исключение.
