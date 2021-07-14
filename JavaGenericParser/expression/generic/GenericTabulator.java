package expression.generic;

import expression.actions.CommonExpression;
import expression.operation.*;
import expression.expressionParser.ExpressionParser;
import expression.expressionParser.ParserException;


import java.util.Map;

public class GenericTabulator implements Tabulator {

    private final Map<String, Operation<?>> tabulator = Map.of(
            "i", new SafeIntegerOperation(),
            "d", new DoubleOperation(),
            "bi", new BigIntegerOperation(),
            "u", new UncheckedIntegerOperation(),
            "l", new LongOperation(),
            "s", new ShortOperation()
    );

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        if (tabulator.containsKey(mode)) {
            return getTable(expression, x1, x2, y1, y2, z1, z2, tabulator.get(mode));
        } else {
            throw new IllegalArgumentException(mode);
        }
    }

    private <T> Object[][][] getTable(String expr, int x1, int x2, int y1, int y2, int z1, int z2, Operation<T> operation) {
        ExpressionParser<T> parser = new ExpressionParser<>(operation);
        CommonExpression<T> expression;
        try {
            expression = parser.parse(expr);
        } catch (final ParserException e) {
            System.out.println("Unable to parse expression" + e.getMessage());
            return null;
        }

        int rangeX = x2 - x1 + 1, rangeY = y2 - y1 + 1, rangeZ = z2 - z1 + 1;
        Object[][][] table = new Object[rangeX][rangeY][rangeZ];

        for (int x = 0; x < rangeX; x++) {
            T first = operation.parseValue(String.valueOf(x + x1));
            for (int y = 0; y < rangeY; y++) {
                T second = operation.parseValue(String.valueOf(y + y1));
                for (int z = 0; z < rangeZ; z++) {
                    T third = operation.parseValue(String.valueOf(z + z1));
                    try {
                        table[x][y][z] = expression.evaluate(first, second, third);
                    } catch (ArithmeticException e) {
                        table[x][y][z] = null;
                    }
                }
            }
        }
        return table;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("You should enter the name of two files");
            return;
        }

        String mode = args[0];
        String expression = args[1];

        try {
            int x1 = -2, x2 = 2;
            int y1 = -2, y2 = 2;
            int z1 = -2, z2 = 2;
            Object[][][] table = (new GenericTabulator()).tabulate(mode, expression, x1, x2, y1, y2, z1, z2);

            System.out.println("Expression : " + expression);
            int rangeX = x2 - x1, rangeY = y2 - y1, rangeZ = z2 - z1;
            for (int x = 0; x <= rangeX; x++) {
                for (int y = 0; y <= rangeY; y++) {
                    for (int z = 0; z <= rangeZ; z++) {
                        System.out.format("x : %d, y : %d, z : %d -> %d\n", (x1 + x), (y1 + y), (z1 + z), table[x][y][z]);
                    }
                    System.out.println();
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
