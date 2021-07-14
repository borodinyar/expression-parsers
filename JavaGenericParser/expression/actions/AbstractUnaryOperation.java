package expression.actions;

import expression.operation.Operation;

public abstract class AbstractUnaryOperation<T> implements CommonExpression<T> {

    private CommonExpression<T> operand;
    protected Operation<T> operation;
    public AbstractUnaryOperation(CommonExpression<T> operand, Operation<T> operation) {
        this.operand = operand;
        this.operation = operation;
    }

    abstract protected T operation(T a);
    @Override
    public T evaluate(T x, T y, T z) {
        return operation(operand.evaluate(x, y, z));
    }

    @Override
    public T evaluate(T x) {
        return operation(operand.evaluate(x));
    }

}
