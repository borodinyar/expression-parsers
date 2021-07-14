package expression.actions;

import expression.operation.Operation;

public class Divide<T> extends AbstractBinaryOperation<T> {

    public Divide(CommonExpression<T> firstOperation, CommonExpression<T> secondOperation, Operation<T> operation) {
        super(firstOperation, secondOperation, operation);
    }

    @Override
    protected T operation(T a, T b) {
        return super.operation.divide(a, b);
    }

}
