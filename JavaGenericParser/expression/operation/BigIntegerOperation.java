package expression.operation;

import java.math.BigInteger;

public class BigIntegerOperation implements Operation<BigInteger> {
    @Override
    public BigInteger parseValue(String value) {
        return new BigInteger(value);
    }

    @Override
    public BigInteger add(BigInteger value1, BigInteger value2) {
        return value1.add(value2);
    }

    @Override
    public BigInteger subtract(BigInteger value1, BigInteger value2) {
        return value1.subtract(value2);
    }

    @Override
    public BigInteger multiply(BigInteger value1, BigInteger value2) {
        return value1.multiply(value2);
    }

    @Override
    public BigInteger divide(BigInteger value1, BigInteger value2) {
        return value1.divide(value2);
    }

    @Override
    public BigInteger negate(BigInteger value) {
        return value.negate();
    }

    @Override
    public BigInteger mod(BigInteger value1, BigInteger value2) {
        return value1.mod(value2);
    }

    @Override
    public BigInteger abs(BigInteger value) {
        return value.abs();
    }

    @Override
    public BigInteger square(BigInteger value) {
        return multiply(value, value);
    }


}
