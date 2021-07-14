package expression.actions;

import expression.operation.Operation;

public abstract class AbstractBinaryOperation<T> implements CommonExpression<T> {

    private final CommonExpression<T> firstOperation, secondOperation;
    protected Operation<T> operation;
    public AbstractBinaryOperation(CommonExpression<T> firstOperation, CommonExpression<T> secondOperation, Operation<T> operation) {
        this.firstOperation = firstOperation;
        this.secondOperation = secondOperation;
        this.operation = operation;
    }

    abstract protected T operation(T a, T b);

    @Override
    public T evaluate(T x, T y, T z) {
        T result1 = firstOperation.evaluate(x, y, z);
        T result2 = secondOperation.evaluate(x, y, z);
        return operation(result1, result2);
    }

    @Override
    public T evaluate(T x) {
        return operation(firstOperation.evaluate(x), secondOperation.evaluate(x));
    }

}
