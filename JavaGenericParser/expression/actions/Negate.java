package expression.actions;

import expression.operation.Operation;

public class Negate<T> extends AbstractUnaryOperation<T> {

    public Negate(CommonExpression<T> operand, Operation<T> operation) {
        super(operand, operation);
    }

    @Override
    protected T operation(T a) {
        return super.operation.negate(a);
    }
}