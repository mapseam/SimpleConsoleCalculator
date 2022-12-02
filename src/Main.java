import java.io.IOException;
import java.lang.String;
import java.util.Scanner;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class Main {
    public static void main(String[] args) {
        System.out.print("Введите выражение: ");
        Scanner input = new Scanner(System.in);

        // Считываем строку
        String sourceExpression = input.nextLine();

        // Если оператор ничего не ввёл, то вызываем исключение
        if (sourceExpression.isEmpty()) {
            try {
                throw new IOException();
            }
            catch (IOException ioEx) {
                System.out.println("throws Exception //т.к. оператор ничего не ввёл");
                return;
            }
        }


        char[] operationSigns = new char[] {'+', '-', '*', '/'};
        // проверяем выраажение на валидность - реализация п.п. 7,8
        if (!isExpressionValid(sourceExpression, operationSigns)) {
            // исключение: формат математической операции не удовлетворяет заданию - два операнда и один оператор (+, -, /, *)
            try {
                throw new IOException();
            } catch (IOException ioEx) {
                System.out.println("throws Exception //т.к. строка не является математической операцией");
                return;
            }
        }

        // Используемые константные массивы
        char[] invalidSigns = new char[] {'.', ','};
        char[] arabicDigits = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] romanDigits = new char[] {'I', 'V', 'X', 'L', 'C', 'D', 'M'};

        // Строка для 1-го операнда, 2-го операнда
        StringBuilder firstOperand = new StringBuilder();
        StringBuilder secondOperand = new StringBuilder();

        boolean isCalculated = true;
        char operationSign = '\0';
        int calculatedResult = 0;

        // переменная для идентификации элемента (операнда) в выражении sourceExpression
        ExpressionElementIndex expressionElementIndex = ExpressionElementIndex.ELEMENT_START;
        // переменна для индентификации используемой числовой системы
        DigitsSystem digitsSystem = DigitsSystem.UNKNOWN_DIGITS_SYSTEM;

        boolean isArabic, isRoman;

        for (int i = 0; i < sourceExpression.length(); i++) {
            // считываем очередной символ
            char currentChar = sourceExpression.charAt(i);

            // если символ - пробел
            if (currentChar == ' ') {
                continue;
            }

            // если символ - не распознан (п.п. 4,7)
            if (isArrayContainsTheChar(invalidSigns, currentChar)) {
                // исключение: символ - непредусмотренный
                try {
                    throw new IOException();
                }
                catch (IOException ioEx) {
                    System.out.println("throws Exception //т.к. символ '" + currentChar + "' - не распознан");
                    isCalculated = false;
                }
            }

            isRoman = false;
            isArabic = isArrayContainsTheChar(arabicDigits, currentChar);
            if (!isArabic)
                isRoman = isArrayContainsTheChar(romanDigits, currentChar);

            // если символ - цифра
            if (isArabic || isRoman) {
                // определяемся с тем, какой элемент выражения - текущий
                if (expressionElementIndex == ExpressionElementIndex.ELEMENT_START) {
                    // при вводе 1-го символа 1-го операнда
                    expressionElementIndex = ExpressionElementIndex.ELEMENT_FIRST_OPERAND;

                    // запоминаем используемую числовую систему
                    if (isArabic) {
                        digitsSystem = DigitsSystem.ARABIC_SYSTEM;
                    }

                    if (isRoman) {
                        digitsSystem = DigitsSystem.ROMAN_SYSTEM;
                    }
                }
                else
                    if (expressionElementIndex == ExpressionElementIndex.ELEMENT_OPERATION_SIGN) {
                        expressionElementIndex = ExpressionElementIndex.ELEMENT_SECOND_OPERAND;

                        // если числовая система второго операнда отличается от числовой системы первого операнда, и наоборот (п.5)
                        if ((isRoman && digitsSystem == DigitsSystem.ARABIC_SYSTEM) || (isArabic && digitsSystem == DigitsSystem.ROMAN_SYSTEM)) {
                            // исключение: калькулятор умеет работать одновременно только с арабскими или римскими цифрами
                            try {
                                throw new IOException();
                            }
                            catch (IOException ioEx) {
                                System.out.println("throws Exception //т.к. калькулятор умеет работать одновременно только с арабскими или римскими цифрами");
                                //isCalculated = false;
                                return;
                            }
                        }
                    }

                // если текущий операнд - тот, что слева от знака операции
                if (expressionElementIndex == ExpressionElementIndex.ELEMENT_FIRST_OPERAND) {
                    // присоединяем символ к концу строки
                    firstOperand.append(currentChar);
                }

                // если текущий операнд - тот, что справа от знака операции
                if (expressionElementIndex == ExpressionElementIndex.ELEMENT_SECOND_OPERAND) {
                    // присоединяем символ к концу строки
                    secondOperand.append(currentChar);
                }
            }

            // если символ - знак операции
            if (isArrayContainsTheChar(operationSigns, currentChar)) {
                // отслеживаем ситуацию, когда знак операции стоит левее 1-го операнда или правее 2-го операнда
                if (expressionElementIndex == ExpressionElementIndex.ELEMENT_START || expressionElementIndex == ExpressionElementIndex.ELEMENT_SECOND_OPERAND) {
                    // исключение: формат математической операции не удовлетворяет заданию - два операнда и один оператор (+, -, /, *)
                    try {
                        throw new IOException();
                    } catch (IOException ioEx) {
                        System.out.println("throws Exception //т.к. формат математической операции не удовлетворяет заданию - два операнда и один оператор (+, -, /, *)");
                        //isCalculated = false;
                        return;
                    }
                }
                else {
                    // если знак операции стоит после 1-го операнда, то учитываем его
                    if (expressionElementIndex == ExpressionElementIndex.ELEMENT_FIRST_OPERAND) {
                        expressionElementIndex = ExpressionElementIndex.ELEMENT_OPERATION_SIGN;
                    }

                    if (operationSign == '\0')
                        operationSign = currentChar;
                    else
                        try {
                            throw new IOException();
                        }
                        catch (IOException ioEx) {
                            System.out.println("throws Exception //т.к. формат математической операции не удовлетворяет заданию - два операнда и один оператор (+, -, /, *)");
                            isCalculated = false;
                        }
                }
            }
        }

        // преобразовании строк в числа
        int firstNumber = 0, secondNumber = 0;

        if (digitsSystem == DigitsSystem.ARABIC_SYSTEM) {
            // отлов возможного исключения при преобразовании
            try {
                firstNumber = Integer.parseInt(firstOperand.toString());
                secondNumber = Integer.parseInt(secondOperand.toString());
            } catch (NumberFormatException nfEx) {
                System.out.println("throws Exception //т.к. строку символов не удалось преобразовать в число");
            }
        }
        else
            if (digitsSystem == DigitsSystem.ROMAN_SYSTEM) {
                firstNumber = translateRomanToArabic(firstOperand.toString());
                secondNumber = translateRomanToArabic(secondOperand.toString());
            }

        //  реализация п.3
        if (!(1 <= firstNumber && firstNumber <= 10) || !(1 <= secondNumber && secondNumber <= 10))  {
            // исключение: калькулятор должен принимать на вход числа от 1 до 10 включительно
            try {
                throw new IOException();
            }
            catch (IOException ioEx) {
                System.out.println("throws Exception //т.к. калькулятор должен принимать на вход числа от 1 до 10 включительно");
                isCalculated = false;
            }
        }

        // вычисление результата
        switch (operationSign) {
            case '+':
                calculatedResult = firstNumber + secondNumber;
                break;
            case '-':
                calculatedResult = firstNumber - secondNumber;
                break;
            case '*':
                calculatedResult = firstNumber * secondNumber;
                break;
            case '/':
                // исключение: возможное деление на 0
                try {
                    calculatedResult = firstNumber / secondNumber;
                }
                catch (ArithmeticException ex) {
                    System.out.println("throws Exception //т.к. ошибка деления на " + secondNumber);
                    //isCalculated = false;
                }
                break;
            default: // реализация п.8
                try {
                    throw new IOException();
                }
                catch (IOException ioEx) {
                    System.out.println("throws Exception //т.к. знак операции - не распознан");
                    isCalculated = false;
                }
        }

        // вывод результата
        System.out.printf("Результат вычисления: " + (isCalculated ? (digitsSystem == DigitsSystem.ROMAN_SYSTEM ? translateArabicToRoman(calculatedResult) : calculatedResult) : "unknown") + "\n");
    }


    // Проверка, содержится ли символ в массиве символов
    private static boolean isArrayContainsTheChar(char[] charArray, char theChar) {
        boolean isContains = false;

        for (char ch : charArray) {
            if (ch == Character.toUpperCase(theChar)) {
                isContains = true;
                break;
            }
        }

        return isContains;
    }


    private static boolean isExpressionValid(String expression, char[] charArray) {
        int idx, counter = 0, len = expression.length()-1;

        for (char ch : charArray) {
            idx = expression.indexOf(ch);

            if (idx != -1 && idx > 0 && idx < len)
                counter++;
        }

        return counter == 1;
    }


    private static int translateRomanToArabic(String operand) {
        String romanNumeral = operand.toUpperCase();
        int resultDigit = 0;

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                resultDigit += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            }
            else {
                i++;
            }
        }

        if (romanNumeral.length() > 0) {
            try {
                throw new IllegalArgumentException();
            }
            catch (IllegalArgumentException ex) {
                System.out.println("throws Exception //т.к. операнд '" + operand + "' не может быть конвертирован в римское число");
            }
        }

        return resultDigit;
    }


    private static String translateArabicToRoman(int number) {
        // реализация п.10
        if ((number < 1) || (number > 4000)) {
            // throws Exception //т.к. в римской системе нет нулевых или отрицательных чисел
            try {
                throw new IllegalArgumentException();
            }
            catch (IllegalArgumentException ex) {
                System.out.println("throws Exception //т.к. в римской системе нет нулевых или отрицательных чисел");
                return "unknown";
            }
        }

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder resultDigit = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);

            if (currentSymbol.getValue() <= number) {
                resultDigit.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            }
            else {
                i++;
            }
        }

        return resultDigit.toString();
    }
}