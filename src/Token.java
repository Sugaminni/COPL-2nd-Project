public class Token {
    private final TokenType type;
    private final String value;

    // Constructor for Token class
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    // Token type getter
    public TokenType getType() {
        return type;
    }

    // Value getter
    public String getValue() {
        return value;
    }

    // toString to print out type and value
    @Override
    public String toString() {
        return type + ": " + value;
    }
}