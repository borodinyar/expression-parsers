package expression.actions;

public interface Expression<T> {
    T evaluate(T x);
}
