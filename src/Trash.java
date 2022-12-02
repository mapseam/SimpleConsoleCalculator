public class Trash {
    private static int translateRomanToArabic(String operand) {
        // Примеры: I - 1, V - 5, X — 10, L — 50, C — 100, D — 500, M — 1000

        //char symbol = '\0';
        int resultDigit = 0;
        int symbolsTotal = operand.length()-1;

        int i = 0;
        while(i <= symbolsTotal) {
            char symbol = operand.charAt(i);

            switch(symbol) {
                case 'I':
                    // если текущий символ - не последний в строке, и следом идут 'V' или 'X'
                    if (i < symbolsTotal && (operand.charAt(i+1) == 'V' || operand.charAt(i+1) == 'X')) {
                        // если следом идёт 'V'
                        if (operand.charAt(i+1) == 'V') {
                            resultDigit += 4;
                        }
                        else
                            // если следом идёт 'X'
                            if (operand.charAt(i+1) == 'X') {
                                resultDigit += 9;
                            }

                        i++;
                    }
                    else {
                        resultDigit += 1;
                        i++;
                    }
                    break;

                case 'V':
                    // если текущий символ - не 1-й и предыдущий - 'I'
                    if (i > 0 && operand.charAt(i-1) == 'I') {
                        // то текущий символ ужЕ учтён в case 'I', и если можно перейти к следующему символу, переходим
                        if (i < symbolsTotal) i++;
                    }
                    else {
                        resultDigit += 5;
                        i++;
                    }
                    break;

                case 'X':
                    // если текущий символ - не 1-й и предыдущий - 'I'
                    if (i > 0 && operand.charAt(i-1) == 'I') {
                        // то текущий символ ужЕ учтён в case 'I', и если можно перейти к следующему символу, переходим
                        if (i < symbolsTotal) i++;
                    }
                    else {
                        resultDigit += 10;
                        i++;
                    }
                    break;

                case 'L': resultDigit += 50; i++; break;
                case 'C': resultDigit += 100; i++; break;
                case 'D': resultDigit += 500; i++; break;
                case 'M': resultDigit += 1000; i++; break;
            }
        }

        return resultDigit;
    }
}
