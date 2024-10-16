package com.syntax_checker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final String KEYWORDS = "abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|double|do|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while";
    private static final String OPERATORS = "\\+|\\-|\\*|\\/|=|<=|>=|!=|==|<|>|!|&|\\||\\^|%|~|\\?";
    private static final String SEPARATORS = "[(){}\\[\\],.;]";
    private static final String IO_CLASS = "System|Scanner|BufferedReader|PrintWriter";
    private static final String IO_METHOD = "out|in|err|println|print|readLine|nextInt|nextDouble";
    private static final String IDENTIFIER = "[a-zA-Z_$][a-zA-Z0-9_$]*";
    private static final String STRING_LITERAL = "\"[^\"]*\"|'[^']'";
    private static final String INTEGER_LITERAL = "\\b[0-9]+\\b";
    private static final String FLOAT_LITERAL = "\\b[0-9]+\\.[0-9]+\\b";
    private static final String BOOLEAN_LITERAL = "\\btrue\\b|\\bfalse\\b";
    private static final String CHAR_LITERAL = "\\\\[nrtbf\\\"']";
    private static final String WHITESPACE = "\\s+";
    private static final String ALL_TOKENS = "(" + KEYWORDS + ")|(" + IO_CLASS + ")|(" + IO_METHOD + ")|("
            + BOOLEAN_LITERAL + ")|(" + IDENTIFIER + ")|(" + OPERATORS + ")|(" + SEPARATORS + ")|(" + STRING_LITERAL
            + ")|(" + FLOAT_LITERAL
            + ")|(" + INTEGER_LITERAL + ")|(" + CHAR_LITERAL + ")|(" + WHITESPACE + ")|(.)";

    public enum TokenType {
        KEYWORD, IO_CLASS, IO_METHOD, BOOLEAN_LITERAL, IDENTIFIER, OPERATOR, SEPARATOR, STRING_LITERAL, FLOAT_LITERAL,
        INTEGER_LITERAL, CHAR_LITERAL, WHITESPACE, UNKNOWN, NEWLINE
    }

    public static class Token {
        public TokenType type;
        public String value;
        public int line;
        public int column;

        public Token(TokenType type, String value, int line, int column) {
            this.type = type;
            this.value = value;
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return String.format("Type: %-20s | Value: %-15s | Line: %-3d | Column: %-3d",
                    type, value, line, column);
        }

    }

    public List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>(); // List to hold tokens
        Pattern pattern = Pattern.compile(ALL_TOKENS); // Compile regex pattern for all tokens
        Matcher matcher = pattern.matcher(code); // Create matcher for input code

        int lineNumber = 1;
        int columnNumber = 1;

        while (matcher.find()) {
            String tokenValue = matcher.group();
            TokenType tokenType = determineTokenType(matcher);

            if (tokenType == TokenType.WHITESPACE) {
                // Update line and column numbers for whitespace
                for (char c : tokenValue.toCharArray()) {
                    if (c == '\n') {
                        tokens.add(new Token(TokenType.NEWLINE, "\\n", lineNumber, columnNumber));
                        lineNumber++;
                        columnNumber = 1;
                    } else {
                        columnNumber++;
                    }
                }
                continue;
            }

            // Handle unknown tokens
            if (tokenType == TokenType.UNKNOWN || tokenValue.contains("#")) {
                throw new LexicalException("Unrecognized token '" + tokenValue + "'", lineNumber, columnNumber);
            }

            tokens.add(new Token(tokenType, tokenValue, lineNumber, columnNumber));
            // Update column count
            columnNumber += tokenValue.length();
        }

        return tokens;
    }

    private TokenType determineTokenType(Matcher matcher) {
        for (int i = 1; i <= matcher.groupCount(); i++) {
            if (matcher.group(i) != null) {
                return TokenType.values()[i - 1];
            }
        }
        return TokenType.UNKNOWN;
    }

    // Custom exception class for lexical errors
    public static class LexicalException extends RuntimeException {
        public LexicalException(String message, int line, int column) {
            super("Lexical error at line " + line + " (column " + column + "): " + message);
        }
    }

    public static void main(String[] args) {
        String code = """
                Scanner sc = new Scanner(System.in);
                System.out.print("");
                System.out.println("");
                System.out.print(myVar);
                System.out.println(myVar);
                System.out.print(_identifier);
                System.out.println(_identifier);
                System.out.print($price);
                System.out.println($price);
                System.out.print(true);
                System.out.println(true);
                System.out.print(false);
                System.out.println(false);
                System.out.print('a');
                System.out.println('a');
                System.out.print(3.1415);
                System.out.println(3.1415);
                System.out.print(10);
                System.out.println(10);
                System.out.print("Sample");
                System.out.println("Sample");
                System.out.print(num + 5);
                System.out.println(num + 5);
                System.out.print(5 * 6);
                System.out.println(5 * 6);
                System.out.print(age >= 18);
                System.out.println(age >= 18);
                System.out.print(firstName + " " + lastName);
                System.out.println(firstName + " " + lastName);
                System.out.print(5 < 10);
                System.out.println(5 < 10);
                System.out.print("The answer is " + (x + y));
                System.out.println("The answer is " + (x + y));
                System.out.print("Value: " + (value / 2));
                System.out.println("Value: " + (value / 2));
                System.out.print("Average: " + (total / count));
                System.out.println("Average: " + (total / count));
                """;
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(code);

        System.out.println("Tokens for the provided code:\n");
        printTokens(tokens);

        String testCode = "Scanner scanner = new Scanner(System.in);\n" +
                "int num = sc.nextInt();\n" +
                "System.out.println(\"Number: \" + num);";
        System.out.println("\nTesting additional code:\n");
        List<Token> additionalTokens = tokenizer.tokenize(testCode);
        printTokens(additionalTokens);
    }

    private static void printTokens(List<Token> tokens) {
        System.out.println("------------------------------------------------------");
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.println("------------------------------------------------------");
    }
}