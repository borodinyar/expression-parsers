package expression.expressionParser;

import expression.actions.TripleExpression;

public interface Parser<T> {
    TripleExpression<T> parse(String expression);
}
