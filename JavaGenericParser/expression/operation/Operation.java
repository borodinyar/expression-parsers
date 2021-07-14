package expression.operation;

public interface Operation<T> {
    T parseValue(String value);

    T add(T value1, T value2);

    T subtract(T value1, T value2);

    T multiply(T value1, T value2);

    T divide(T value1, T value2);

    T negate(T value);

    T mod(T value1, T value2);

    T abs (T value);

    T square (T value);
}
