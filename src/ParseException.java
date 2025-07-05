    // Exception thrown when the parser finds a syntax error
public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }
}
