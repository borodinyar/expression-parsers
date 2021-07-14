
package expression.operation;

import expression.exceptions.DivisionByZero;
import expression.exceptions.Overflow;
import expression.exceptions.Underflow;

public class UncheckedIntegerOperation implements Operation<Integer> {
    @Override
    public Integer parseValue(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Integer add(Integer value1, Integer value2) {
        return value1 + value2;
    }

    @Override
    public Integer subtract(Integer value1, Integer value2) {
        return value1 - value2;
    }

    @Override
    public Integer multiply(Integer value1, Integer value2) {
        return value1 * value2;
    }

    @Override
    public Integer divide(Integer value1, Integer value2) {
        return value1 / value2;
    }

    @Override
    public Integer negate(Integer value) {
        return -value;
    }

    @Override
    public Integer mod(Integer value1, Integer value2) {
        return value1 % value2;
    }

    @Override
    public Integer abs(Integer value) {
        return Math.abs(value);
    }

    @Override
    public Integer square(Integer value) {
        return multiply(value, value);
    }
}
