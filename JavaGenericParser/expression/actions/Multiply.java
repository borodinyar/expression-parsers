package expression.actions;

import expression.operation.Operation;

public class Multiply<T> extends AbstractBinaryOperation<T> {

    public Multiply(CommonExpression<T> firstOperation, CommonExpression<T> secondOperation, Operation<T> operation) {
        super(firstOperation, secondOperation, operation);
    }

    @Override
    protected T operation(T a, T b) {
        return super.operation.multiply(a, b);
    }

}
