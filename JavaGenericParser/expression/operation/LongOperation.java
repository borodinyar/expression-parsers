package expression.operation;

public class LongOperation implements Operation<Long> {

    @Override
    public Long parseValue(String value) {
        return Long.parseLong(value);
    }

    @Override
    public Long add(Long value1, Long value2) {
        return value1 + value2;
    }

    @Override
    public Long subtract(Long value1, Long value2) {
        return value1 - value2;
    }

    @Override
    public Long multiply(Long value1, Long value2) {
        return value1 * value2;
    }

    @Override
    public Long divide(Long value1, Long value2) {
        return value1 / value2;
    }

    @Override
    public Long negate(Long value) {
        return -value;
    }

    @Override
    public Long mod(Long value1, Long value2) {
        return value1 % value2;
    }

    @Override
    public Long abs(Long value) {
        if (value < 0) {
            return negate(value);
        }
        return value;
    }

    @Override
    public Long square(Long value) {
        return multiply(value, value);
    }

}
