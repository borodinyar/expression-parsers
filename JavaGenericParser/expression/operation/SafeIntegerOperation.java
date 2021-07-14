package expression.operation;

import expression.exceptions.DivisionByZero;
import expression.exceptions.Overflow;
import expression.exceptions.Underflow;

public class SafeIntegerOperation implements Operation<Integer>{
    @Override
    public Integer parseValue(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Integer add(Integer value1, Integer value2) {
        checkAddExceptions(value1, value2);
        return value1 + value2;
    }

    @Override
    public Integer subtract(Integer value1, Integer value2) {
        checkSubtractExceptions(value1, value2);
        return value1 - value2;
    }

    @Override
    public Integer multiply(Integer value1, Integer value2) {
        checkMultiplyExceptions(value1, value2);
        return value1 * value2;
    }

    @Override
    public Integer divide(Integer value1, Integer value2) {
        checkDivideExceptions(value1, value2);
        return value1 / value2;
    }

    @Override
    public Integer negate(Integer value) {
        checkNegateExceptions(value);
        return -value;
    }

    @Override
    public Integer mod(Integer value1, Integer value2) {
        checkDivideExceptions(value1, value2);
        return value1 % value2;
    }

    @Override
    public Integer abs(Integer value) {
        if (value < 0) {
            return negate(value);
        }
        return value;
    }

    @Override
    public Integer square(Integer value) {
        return multiply(value, value);
    }

    private void checkAddExceptions(int value1, int value2) {
        if (value2 > 0 && value1 > Integer.MAX_VALUE - value2) {
            throw new Overflow("Overflow: " + value1 + " + " + value2);
        } else if (value2 < 0 && value1 < Integer.MIN_VALUE - value2) {
            throw new Underflow("Underflow: " + value1 + " + (" + value2 + ")");
        }
    }

    private void checkSubtractExceptions(int value1, int value2) {
        if (value2 > 0 && value1 < Integer.MIN_VALUE + value2) {
            throw new Overflow("Underflow: " + value1 + " - " + value2);
        } else if (value2 < 0 && value1 > Integer.MAX_VALUE + value2) {
            throw new Underflow("Overflow: " + value1 + " - (" + value2 + ")");
        }
    }

    private void checkMultiplyExceptions(int value1, int value2) {
        if (value1 > 0 && value2 > 0 && Integer.MAX_VALUE / value2 < value1 ||
                value1 < 0 && value2 < 0 && Integer.MAX_VALUE / value2 > value1) {
            throw new Overflow("Overflow: " + value1 + " * " + value2);
        }
        if (value1 > 0 && value2 < 0 && Integer.MIN_VALUE / value1 > value2 ||
                value1 < 0 && value2 > 0 && Integer.MIN_VALUE / value2 > value1) {
            throw new Underflow("Underflow: " + value1 + " * " + value2);
        }
    }

    private void checkDivideExceptions(int value1, int value2) {
        if (value1 == Integer.MIN_VALUE && value2 == -1) {
            throw new Overflow("Overflow: " + Integer.MIN_VALUE + " / " + "(-1)");
        } else if (value2 == 0) {
            throw new DivisionByZero("Division by zero");
        }
    }

    private void checkNegateExceptions(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new Overflow("Overflow: -" + value);
        }
    }
}
