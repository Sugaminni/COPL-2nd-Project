import com.google.gson.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

// Parses token JSON into a parse tree
public class Parser {
    private List<Token> tokens;                // List of tokens loaded from JSON file
    private int current = 0;                   // Index of the current token
    private Scanner scanner;                   // Reference to scanner for identifier checks

    // Links keywords to statement parsing functions
    private final Map<String, Supplier<ParseTreeNode>> statementParsers = new HashMap<>();

    public Parser() {
        // Registers implemented statements
        statementParsers.put("define", this::parseDefineSt);
        statementParsers.put("display", this::parseDisplaySt);
        statementParsers.put("set", this::parseSetSt);
        statementParsers.put("import", this::parseImportSt);
        statementParsers.put("exit", this::parseExitSt);

        // Registers stubs for keywords not implemented
        String[] stubs = {
                "if", "else", "while", "print", "return", "main", "declare", "length",
                "begin", "end", "function", "procedure", "program", "class",
                "interface", "list", "object", "const", "true", "false", "and", "or", "not"
        };
        for (String keyword : stubs) {
            statementParsers.put(keyword, () -> stub(keyword + "Statement"));
        }
    }

    // Loads tokens from JSON file and parses the program
    public ParseTreeNode begin(String jsonFile) throws IOException {
        Gson gson = new Gson();
        Reader reader = new FileReader(jsonFile);
        Token[] tokenArray = gson.fromJson(reader, Token[].class);
        tokens = Arrays.asList(tokenArray);
        reader.close();

        return parseProgram();
    }

    // Parses the SCL program
    private ParseTreeNode parseProgram() {
        ParseTreeNode node = new ParseTreeNode("Program");
        while (!isAtEnd()) {
            String value = peekToken().getValue().toLowerCase();
            if (!statementParsers.containsKey(value)) {
                Token t = getNextToken();
                node.addChild(node("UnknownStatement", t.getValue()));
                continue;
            }
            ParseTreeNode stmtNode = parseStatement();
            if (stmtNode != null) {
                node.addChild(stmtNode);
            }
        }
        return node;
    }

    // Determines and calls the parser for the current statement
    private ParseTreeNode parseStatement() {
        if (isAtEnd()) return null;
        String keyword = peekToken().getValue().toLowerCase();
        Supplier<ParseTreeNode> parser = statementParsers.get(keyword);
        if (parser != null) {
            getNextToken();
            return parser.get();
        } else {
            Token token = getNextToken();
            return node("UnknownStatement", token.getValue());
        }
    }

    // Parses define statement
    private ParseTreeNode parseDefineSt() {
        Token id = consume(TokenType.IDENTIFIER, "Expected identifier after 'define'.");
        expectSequence("of", "type");
        Token type = consume(TokenType.KEYWORD, "Expected type keyword.");
        return node("DefineStatement", "define", id.getValue(), "of", "type", type.getValue());
    }

    // Parses display statement
    private ParseTreeNode parseDisplaySt() {
        Token id = consume(TokenType.IDENTIFIER, "Expected identifier after 'display'.");

        // Example usage of identifierExists (if scanner connected)
        if (scanner != null && !scanner.identifierExists(id.getValue())) {
            System.out.println("Warning: identifier '" + id.getValue() + "' not declared.");
        }

        return node("DisplayStatement", "display", id.getValue());
    }

    // Parses set statement
    private ParseTreeNode parseSetSt() {
        Token id = consume(TokenType.IDENTIFIER, "Expected identifier after 'set'.");
        consume("=", "Expected '=' after identifier.");
        Token value = getNextToken();
        return node("SetStatement", "set", id.getValue(), "=", value.getValue());
    }

    // Parses import statement
    private ParseTreeNode parseImportSt() {
        Token id = consume(TokenType.IDENTIFIER, "Expected module name after 'import'.");
        return node("ImportStatement", "import", id.getValue());
    }

    // Parses exit statement
    private ParseTreeNode parseExitSt() {
        return node("ExitStatement", "exit");
    }

    // Returns a stub parse tree node for unimplemented statements
    private ParseTreeNode stub(String statementName) {
        return node(statementName, "Not implemented yet");
    }

    // Creates a node with given label and child strings
    private ParseTreeNode node(String label, String... children) {
        ParseTreeNode node = new ParseTreeNode(label);
        for (String child : children) {
            node.addChild(new ParseTreeNode(child));
        }
        return node;
    }

    // Consumes expected keywords in order
    private void expectSequence(String... words) {
        for (String word : words) {
            consume(word, "Expected '" + word + "'.");
        }
    }

    // Checks if end of token list is reached
    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    // Peeks at the current token
    private Token peekToken() {
        return tokens.get(current);
    }

    // Goes to next token
    private Token getNextToken() {
        if (!isAtEnd()) current++;
        return getPreviousToken();
    }

    // Gets the last token
    private Token getPreviousToken() {
        return tokens.get(current - 1);
    }

    // Matches and consumes a specific string value
    private boolean match(String value) {
        if (isAtEnd()) return false;
        String tokenValue = peekToken().getValue();
        if (tokenValue.equalsIgnoreCase(value)) {
            getNextToken();
            return true;
        }
        return false;
    }

    // Consumes a token of the expected type
    private Token consume(TokenType type, String message) {
        if (check(type)) return getNextToken();
        throw error(peekToken(), message);
    }

    // Consumes a token matching a specific value
    private void consume(String value, String message) {
        if (match(value)) return;
        throw error(peekToken(), message);
    }

    // Checks if the current token matches a type
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peekToken().getType() == type;
    }

    // Creates a parse error
    private ParseException error(Token token, String message) {
        return new ParseException("Parse error at token '" + token.getValue() + "': " + message);
    }

    // Returns the list of tokens
    public List<Token> getTokens() {
        return tokens;
    }
}
