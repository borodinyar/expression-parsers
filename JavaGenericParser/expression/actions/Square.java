package expression.actions;

import expression.operation.Operation;

public class Square <T> extends AbstractUnaryOperation<T>  {

    public Square(CommonExpression<T> operand, Operation<T> operation) {
        super(operand, operation);
    }

    @Override
    protected T operation(T a) {
        return super.operation.square(a);
    }
}