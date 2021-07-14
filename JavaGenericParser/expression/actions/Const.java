package expression.actions;

public class Const<T> implements CommonExpression<T> {

    private T x;

    public Const(T x) {
        this.x = x;
    }

    @Override
    public T evaluate(T x) {
        return this.x;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return this.x;
    }
}
