package expression.expressionParser;

public class StringSource implements CharSource {
    private final String data;
    private int pos;

    public StringSource(final String data) {
        this.data = data;
    }

    @Override
    public boolean hasNext() {
        return pos < data.length();
    }

    @Override
    public char next() {
        return data.charAt(pos++);
    }

    @Override
    public ParserException error(final String message) {
        return new ParserException(pos + ": " + message);
    }

    @Override
    public int getPosition() {
        return pos;
    }

}
