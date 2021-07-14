package expression.operation;

public class ShortOperation implements Operation<Short> {
    @Override
    public Short parseValue(String value) {
        return (short)Integer.parseInt(value);
    }

    @Override
    public Short add(Short value1, Short value2) {
        return (short) (value1 + value2);
    }

    @Override
    public Short subtract(Short value1, Short value2) {
        return (short) (value1 - value2);
    }

    @Override
    public Short multiply(Short value1, Short value2) {
        return (short) (value1 * value2);
    }

    @Override
    public Short divide(Short value1, Short value2) {
        return (short) (value1 / value2);
    }

    @Override
    public Short negate(Short value) {
        return (short) (-value);
    }

    @Override
    public Short mod(Short value1, Short value2) {
        return (short) (value1 % value2);
    }

    @Override
    public Short abs(Short value) {
        return (short) Math.abs(value);
    }

    @Override
    public Short square(Short value) {
        return multiply(value, value);
    }
}
