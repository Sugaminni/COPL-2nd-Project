import java.io.*;
import java.util.*;
import java.util.regex.*;

// Scanner class for tokenizing an SCL source file.
// Recognizes keywords, identifiers, numbers, operators, and symbols.
public class Scanner {
    // Set of recognized keywords in the SCL language subset
    private static final Set<String> KEYWORDS = Set.of(
            "import", "define", "display", "set", "exit",
            "of", "type", "integer", "double", "char",
            "if", "else", "while", "print", "return",
            "main", "declare", "length", "begin", "end",
            "function", "procedure", "program", "class",
            "interface", "list", "object", "const",
            "true", "false", "and", "or", "not"
    );

    // Stores all tokens found in the file
    private final List<Token> tokens = new ArrayList<>();

    // Stores all unique identifiers (variable names)
    private final List<String> identifiers = new ArrayList<>();

    // Stores all unique keywords used in the source file
    private final List<String> keywordsUsed = new ArrayList<>();

    // return the list of all tokens found in the source file
    public List<Token> getTokens() {
        return tokens;
    }

    // return the list of unique identifiers (variables) found in the source file
    public List<String> getIdentifiers() {
        return identifiers;
    }

    // return the list of unique keywords used in the source file
    public List<String> getKeywordsUsed() {
        return keywordsUsed;
    }

    /**
     * Scans a given SCL source file and extracts tokens into categorized lists.
     * Param filePath the path to the .scl file
     * throws IOException if the file cannot be read
     */
    public void scanFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        // Makes pattern match words, operators, and symbols
        Pattern tokenPattern = Pattern.compile("\\w+|==|!=|<=|>=|[+\\-*/=<>;(){}]");

        // Reads the file line-by-line and checks for a match
        while ((line = reader.readLine()) != null) {
            Matcher matcher = tokenPattern.matcher(line);

            // Extract all tokens from the line if match is found
            while (matcher.find()) {
                String token = matcher.group();

                // Puts tokens into a category
                if (KEYWORDS.contains(token)) {
                    tokens.add(new Token(TokenType.KEYWORD, token));
                    if (!keywordsUsed.contains(token)) keywordsUsed.add(token);
                } else if (token.matches("[a-zA-Z_]\\w*")) {
                    tokens.add(new Token(TokenType.IDENTIFIER, token));
                    if (!identifiers.contains(token)) identifiers.add(token);
                } else if (token.matches("\\d+(\\.\\d+)?")) {
                    tokens.add(new Token(TokenType.NUMBER, token));
                } else if (token.matches("==|!=|<=|>=|[+\\-*/=<>]")) {
                    tokens.add(new Token(TokenType.OPERATOR, token));
                } else if (token.matches("[;(){}]")) {
                    tokens.add(new Token(TokenType.SYMBOL, token));
                } else {
                    tokens.add(new Token(TokenType.UNKNOWN, token));
                }
            }
        }
        reader.close();
    }

    // Checks if identifier was found while scanning, returns true if it exists
    public boolean identifierExists(String identifier) {
        return identifiers.contains(identifier);
    }
}
