package expression.operation;

public class DoubleOperation implements Operation<Double> {
    @Override
    public Double parseValue(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public Double add(Double value1, Double value2) {
        return value1 + value2;
    }

    @Override
    public Double subtract(Double value1, Double value2) {
        return value1 - value2;
    }

    @Override
    public Double multiply(Double value1, Double value2) {
        return value1 * value2;
    }

    @Override
    public Double divide(Double value1, Double value2) {
        return value1 / value2;
    }

    @Override
    public Double negate(Double value) {
        return -value;
    }

    @Override
    public Double mod(Double value1, Double value2) {
        return value1 % value2;
    }

    @Override
    public Double abs(Double value) {
        if (value < 0) {
            return negate(value);
        }
        return value;
    }

    @Override
    public Double square(Double value) {
        return multiply(value, value);
    }

}
