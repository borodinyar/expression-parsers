package expression.actions;

import expression.operation.Operation;

public class Abs <T> extends AbstractUnaryOperation<T>  {

    public Abs(CommonExpression<T> operand, Operation<T> operation) {
        super(operand, operation);
    }

    @Override
    protected T operation(T a) {
        return super.operation.abs(a);
    }
}