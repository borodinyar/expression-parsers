package expression.expressionParser;


import expression.actions.*;
import expression.exceptions.*;
import expression.operation.Operation;

import java.util.Set;

public class ExpressionParser<T> implements Parser<T> {
    private StringSource source;
    private char current;
    private int balanceBrackets;
    private Operation<T> operation;
    private int action; // -1 - nothing, 0 - const, 1 - variable, 2 - operation
    private final Set<Character> SIGNS = Set.of('+', '-', '*', '/', '(', ')', '&', '^', '|');

    public ExpressionParser(Operation<T> operation) {
        this.operation = operation;
    }

    private void nextChar() {
        current = source.hasNext() ? source.next() : '\0';
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(current)) {
            nextChar();
        }
    }

    public CommonExpression<T> parse(String expression) {
        source = new StringSource(expression);
        return parse(source);
    }

    public CommonExpression<T> parse(StringSource expression) {
        nextChar();
        skipWhitespace();
        balanceBrackets = 0;
        action = -1;
        CommonExpression<T> result = parseAdd();
        if (balanceBrackets > 0) {
            throw new BracketException("Extra open parenthesis");
        }
        return result;
    }

    private boolean checkSymbol(char ch) {
        skipWhitespace();
        if (current == ch) {
            nextChar();
            return true;
        }
        return false;
    }

    private boolean checkOperation() {
        return !Character.isWhitespace(current) && current != '(' && current != '-';
    }

    private CommonExpression<T> parseAdd() {
        CommonExpression<T> result = parseMultiplyAndMod();
        skipWhitespace();
        while (true) {
            if (checkSymbol('+')) {
                action = 0;
                result = new Add<>(result, parseMultiplyAndMod(), operation);
            } else if (checkSymbol('-')) {
                action = 0;
                result = new Subtract<>(result, parseMultiplyAndMod(), operation);
            }   else if (!source.hasNext() || current == ')') {
                checkCloseBrackets();
                return result;
            } else {
                if (action == 0) {
                    throw new IllegalConstException("Invalid symbol in const: " + current + " on position: " + source.getPosition());
                } else {
                    throw new IllegalVariableException("Invalid symbol in variable: " + current + " on position: " + source.getPosition());
                }
            }
        }
    }

    private CommonExpression<T> parseMultiplyAndMod() {
        CommonExpression<T> result = parsePrimary();
        skipWhitespace();
        while (true) {
            skipWhitespace();
            if (checkSymbol('*')) {
                action = 0;
                result = new Multiply<>(result, parsePrimary(), operation);
            } else if (checkSymbol('/')) {
                action = 0;
                result = new Divide<>(result, parsePrimary(), operation);
            } else if (parseVariable().equals("mod")) {
                action = 0;
                if (checkOperation()) {
                    throw new IllegalOperationException("Invalid operation's format: mod " + current);
                }
                result = new Mod<>(result, parsePrimary(), operation);
            } else {
                return result;
            }
        }
    }

    private boolean between(char a, char b) {
        return (current >= a && current <= b);
    }

    private Const<T> parseConst(String prefix) {
        StringBuilder number = new StringBuilder(prefix);
        while (between('0', '9')) {
            number.append(current);
            nextChar();
        }
        return new Const<>(operation.parseValue(number.toString()));
    }

    private String parseVariable() {
        skipWhitespace();
        StringBuilder var = new StringBuilder();
        while (between('a', 'z')) {
            var.append(current);
            nextChar();
        }
        return var.toString();
    }

    private void checkCloseBrackets() {
        if (checkSymbol(')')) {
            balanceBrackets--;
        }
        if (balanceBrackets < 0) {
            throw new BracketException("Unnecessary close bracket on position: " + source.getPosition());
        }
    }

    private CommonExpression<T> parsePrimary() {
        CommonExpression<T> result;
        skipWhitespace();
        if (checkSymbol('(')) {
            balanceBrackets++;
            result = parseAdd();
        } else if (checkSymbol('-')) {
            action = 0;
            result = between('0', '9') ? parseConst("-") : new Negate<>(parsePrimary(), operation);
        } else if (between('0', '9')) {
            action = 0;
            result = parseConst("");
        } else if (between('a', 'z')) {
            String var = parseVariable();
            switch (var) {
                case "abs":
                    action = 2;
                    result = new Abs<>(parsePrimary(), operation);
                    break;
                case "square":
                    action = 2;
                    result = new Square<>(parsePrimary(), operation);
                    break;
                case "x":
                case "y":
                case "z":
                    action = 1;
                    if (current != '\0' && !Character.isWhitespace(current) && !SIGNS.contains(current)) {
                        throw new IllegalVariableException("Invalid symbol in variable: " + current + " on position: " + source.getPosition());
                    }
                    result = new Variable<>(var);
                    break;
                default:
                    if (action == 2) {
                        throw new IllegalVariableException("Invalid variable's name: " + var + " on position: " + source.getPosition());
                    } else {
                        throw new IllegalOperationException("Invalid operation: " + var + current + " on position: " + source.getPosition());
                    }
            }
        } else if (checkSymbol('~')) {
            action = 0;
            result = new Negate<>(parsePrimary(), operation);
        } else {
            if (action == 2) {
                throw new WaitingExpressionException("Waiting argument in expression before symbol: " + current + " on position: " + source.getPosition());
            } else {
                throw new WaitingExpressionException("Waiting operation in expression before symbol: " + current + " on position: " + source.getPosition());
            }
        }
        skipWhitespace();
        return result;
    }

}
