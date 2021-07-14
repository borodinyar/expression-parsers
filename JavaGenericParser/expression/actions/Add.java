package expression.actions;

import expression.operation.Operation;

public class Add<T> extends AbstractBinaryOperation<T> {

    public Add(CommonExpression<T> firstOperation, CommonExpression<T> secondOperation, Operation<T> operation) {
        super(firstOperation, secondOperation, operation);
    }

    @Override
    protected T operation(T a, T b) {
        return super.operation.add(a, b);
    }

}
