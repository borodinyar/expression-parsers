"use strict"

let operations = new Map();

function createExpression(expression, evaluate, diff, toSting, prefix, postfix) {
    expression.prototype.evaluate = evaluate;
    expression.prototype.diff = diff;
    expression.prototype.toString = toSting;
    expression.prototype.prefix = prefix? prefix : toSting;
    expression.prototype.postfix = postfix? postfix : toSting;

    return expression;
}

const AbstractOperation = createExpression(
    function (...operands) {
        this.operands = operands;
    },

    function (...vars) {
        return this.operation (...this.operands.map(function (operand) {
            return operand.evaluate(...vars)
        }))
    },

    function (variable) {
        return this.derivative (...this.operands)(...this.operands.map(function (operand) {
            return operand.diff(variable);
        }));
    },

    function () {
        return this.operands.map(function (operand) {
            return operand.toString();
        }).join(" ") + " " + this.sign;
    },

    function () {
        return "(" + this.sign + " " + this.operands.map(function (operand) {
            return operand.prefix();
        }).join(" ") + ")";
    },


    function () {
        return "(" + this.operands.map(function (operand) {
            return operand.postfix();
        }).join(" ")  + " " + this.sign + ")";
    }
    )


function createOperation(operation, sign, derivative) {
    const Operation = function (...operands) {
        AbstractOperation.call(this, ...operands)
    }
    Operation.prototype = Object.create(AbstractOperation.prototype);
    Operation.prototype.operation = operation;
    Operation.prototype.sign = sign;
    Operation.prototype.derivative = derivative;
    operations.set(sign, Operation)
    return Operation;
}

const Const = createExpression(
    function (value) {
        this.value = value;
    },

    function () {
        return this.value
    },

    () => ConstZero,

    function () {
        return this.value.toString();
    }
    )

const ConstZero = new Const(0);
const ConstOne = new Const(1);
const ConstTwo = new Const(2);
const ConstThree = new Const(3);

const vars = ["x", "y", "z"];

const Variable = createExpression(
    function (name) {
        this.name = name;
        this.index = vars.indexOf(name);
    },
    function (...args) {
        return args[this.index];
    },

    function(variable) {
        return variable === this.name ? ConstOne : ConstZero;
    },
    function () {
        return this.name;
    }
    )

const Add = createOperation(
    (first, second) => (first + second),
    "+",
    (first, second) => (firstDerivative, secondDerivative) => new Add(firstDerivative, secondDerivative)
    )

const Subtract = createOperation(
    (first, second) => (first - second),
    "-",
    (first, second) => (firstDerivative, secondDerivative) => new Subtract(firstDerivative, secondDerivative)
    )

const Multiply = createOperation(
    (first, second) => (first * second),
    "*",
    (first, second) => (firstDerivative, secondDerivative) =>
        new Add(new Multiply(firstDerivative, second),
            new Multiply(first, secondDerivative))
    )

const Divide = createOperation(
    (first, second) => (first / second),
    "/",
    (first, second) => (firstDerivative, secondDerivative) =>
        new Divide(
            new Subtract(
                new Multiply(firstDerivative, second),
                new Multiply(first, secondDerivative)),
            new Multiply(second, second))
    )

const Negate = createOperation(
    (a) => (-a),
    "negate",
    (argument) => (argumentDerivative) => new Negate(argumentDerivative)
    )

const Cube = createOperation(
    (a) => (a ** 3),
    "cube",
    (argument) => (argumentDerivative) =>
        new Multiply(ConstThree,
            new Multiply(argumentDerivative, new Multiply(argument, argument)))
    )

const Cbrt = createOperation(
    (a) => (Math.cbrt(a)),
    "cbrt",
    (argument) => (argumentDerivative) =>
        new Divide(argumentDerivative,
            new Multiply(ConstThree, new Cbrt(new Multiply(argument, argument))))
    )

const Sumsq = createOperation(
    (...args) => (args.reduce((acum, curr) => (acum + curr * curr), 0)),
    "sumsq",
    (...argument) => (...argumentDerivative) => {
        if (argument.length === 0) {
            return ConstZero;
        }
        let result = new Multiply(argument[0], argumentDerivative[0]);
        for (let i = 1; i < argument.length; ++i) {
            result = new Add(result, new Multiply(argument[i], argumentDerivative[i]));
        }
        return new Multiply(result, ConstTwo);
    }
    )

const Length = createOperation(
    (...args) => (Math.sqrt(args.reduce((acum, curr) => (acum + curr * curr), 0))),
    "length",
    (...argument) => (...argumentDerivative) => argument.length === 0 ? ConstZero : new Divide(new Sumsq(...argument).derivative(...argument)(...argumentDerivative),
        new Multiply(ConstTwo, new Length(...argument)))
    )

const parse = input => {
    let array = input.trim().split(/\s+/)

    let stack = [];
    for (let element of array) {
        if (operations.has(element)) {
            let operands = [];

            for (let i = 0; i < operations.get(element).prototype.operation.length; i++) {
                operands.push(stack.pop());
            }

            let operation = operations.get(element);
            stack.push(new operation(...operands.reverse()));
        } else {
            stack.push(isNaN(parseFloat(element)) ? new Variable(element) : new Const(+element));
        }
    }

    return stack[0];
}


const createException = function (name, Message) {
    const exception = function (...args) {
        this.message = Message(...args);
        this.name = name;
    };
    exception.prototype = new Error;

    return exception;
};

const OperationExpectedException = createException(
    "UnexpectedOperationException",
    (index, token) => "Unexpected operation : '" + token + "' at index " + (index + 1)
    );

const ExpressionEndExpectedException = createException(
    "ExpressionEndExpectedException",
    (index, token) => "End of expression expected : '" + token + "' at index " + (index + 1)
    );

const ClosingParenthesisMissingException = createException(
    "ClosingParenthesisMissingException",
    (index, token) => "Closing parenthesis expected : '" + token + "' at index " + (index + 1)
    );

const MissingOperationException = createException(
    "MissingOperationException",
    (index, token) => "Operation missed : '" + token + "' at index " + (index + 1)
    );

const OperandExpectedException = createException(
    "OperandExpectedException",
    (index, token) => "Operand expected : '" + token + "' at index " + (index + 1)
    );

const InvalidOperandsAmountException = createException(
    "InvalidOperandsAmountException",
    (symb, token, index) => "Found operands amount invalid : " + token + " expected " + symb + " at index " + (index + 1)
    );

function Sourse (data) {
    this.data = data;
    this.pos = 0;

    this.hasNext = function () {
        return this.pos < this.data.length;
    }
    this.next = function() {
        return this.data[this.pos++];
    }
    this.getChar = function () {
        return this.data[this.pos];
    }
    this.getPosition = function () {
        return this.pos;
    }

}

const isSpace = function(ch) {
    return ch === ' ';
}

const isNumber = function (s) {
    return ((s !== "") && (!isNaN(+s)));
}

function parsePrefix (expression) {
    return AbstractParser (new Sourse(expression), "prefix");
}

function parsePostfix (expression) {
    return AbstractParser (new Sourse(expression), "postfix");
}

function AbstractParser (sourse, mode) {
    let skipWhitespace = () => {
        while (isSpace(sourse.getChar())) {
            sourse.next();
        }
    }

    let nextToken = function () {
        skipWhitespace();
        let result = "";
        if (sourse.getChar() === "(" || sourse.getChar() === ")") {
            result += sourse.next();
        } else {
            while (sourse.getChar() !== '(' &&  sourse.getChar() !== ')'
            && sourse.hasNext() && !isSpace(sourse.getChar())) {
                result += sourse.next();
            }
        }
        return result;
    }

    let parseArgument = () => {
        if (token === '(') {
            return parseOperations();
        } else if (isNumber(token)) {
            let result = new Const(parseFloat(token));
            token = nextToken();
            return result;
        } else if (vars.includes(token)) {
            let result = new Variable(token);
            token = nextToken();
            return result;
        }
        throw new OperandExpectedException(sourse.getPosition(), token).toString();
    }

    let parseOperations = () => {
        if (token === "") {
            throw new MissingOperationException(sourse.getPosition(), token).toString();
        }
        if (token !== "(") {
            return parseArgument();
        }
        token = nextToken();
        let expressionArguments = [];
        let operation, operationLength;
        if (mode === "prefix") {
            if (!(operations.has(token))) {
                throw new OperationExpectedException(sourse.getPosition(), token).toString();
            }
            operation = operations.get(token);
            token = nextToken();
            operationLength = operation.prototype.operation.length;
            while (token !== ')' && token !== '') {
                if (operationLength !== 0 && expressionArguments.length >= operationLength) {
                    break;
                }
                expressionArguments.push(parseArgument());
            }
        } else {
            while (!(operations.has(token))) {
                expressionArguments.push(parseArgument());
            }
            operation = operations.get(token);
            operationLength = operation.prototype.operation.length;
            token = nextToken();
        }
        if (operationLength !== 0 && expressionArguments.length !== operationLength) {
            throw new InvalidOperandsAmountException(operationLength, expressionArguments.length, sourse.getPosition()).toString();
        }
        if (token !== ')') {
            throw new ClosingParenthesisMissingException(sourse.getPosition(), token).toString();
        }
        token = nextToken();
        return new operation(...expressionArguments);
    }

    let token = nextToken();
    let expression = parseOperations();
    if (token !== "") {
        throw new ExpressionEndExpectedException(sourse.getPosition(), token).toString();
    }
    return expression;
}
