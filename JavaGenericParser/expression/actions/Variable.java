package expression.actions;

public class Variable<T> implements CommonExpression<T> {

    private String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public T evaluate(T x) {
        return null;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        if (name.equals("x")) {
            return x;
        } else if (name.equals("y")) {
            return y;
        } else {
            return z;
        }
    }
}
