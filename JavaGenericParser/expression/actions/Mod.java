package expression.actions;

import expression.operation.Operation;

public class Mod <T> extends AbstractBinaryOperation<T> {
    public Mod(CommonExpression<T> firstOperation, CommonExpression<T> secondOperation, Operation<T> operation) {
        super(firstOperation, secondOperation, operation);
    }

    @Override
    protected T operation(T a, T b) {
        return super.operation.mod(a, b);
    }

}