package expression.expressionParser;

public interface CharSource {
    boolean hasNext();
    char next();
    ParserException error(final String message);
    int getPosition();
}
