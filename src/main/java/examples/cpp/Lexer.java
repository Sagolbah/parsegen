package examples.cpp;
import java.util.HashMap;
import java.util.Map;
public class Lexer {
    private String expr;
    private StringBuilder consumed = new StringBuilder();
    private Map<Integer, String> regexes = new HashMap<>();
    private Map<Integer, String> stricts = new HashMap<>();
    private int endId = 5;
    private int stringPtr;
    private Token curToken;
    private String lastConsumed;
	public Lexer() {
		regexes.put(0, "[a-zA-Z]+");
		stricts.put(1, ";");
		stricts.put(2, ",");
		stricts.put(3, "*");
		stricts.put(4, "&");
	}
	public void setExpr(final String expr) {
        this.expr = expr;
    }

    private void skip() {
        while (stringPtr < expr.length() && Character.isWhitespace(expr.charAt(stringPtr))) {
            stringPtr++;
        }
    }

    public Token getCurToken() {
        return curToken;
    }

    public void nextToken() {
        skip();
        if (stringPtr >= expr.length()) {
            curToken = new Token(endId, "");
            return;
        }
        while (true) {
            consumed.append(expr.charAt(stringPtr++));
            String s = consumed.toString();
            for (int i = 0; i < endId; i++) {
                if (regexes.containsKey(i) && s.matches(regexes.get(i))) {
                    while (s.matches(regexes.get(i)) && stringPtr < expr.length()) {
                        consumed.append(expr.charAt(stringPtr++));
                        s = consumed.toString();
                    }
                    if (!s.matches(regexes.get(i))) {
                        // pop back
                        consumed.setLength(consumed.length() - 1);
                        s = consumed.toString();
                        stringPtr--;
                    }
                    flush(s, i);
                    return;
                } else if (stricts.containsKey(i) && s.equals(stricts.get(i))) {
                    flush(s, i);
                    return;
                }
            }
        }
    }

    private void flush(String s, int i) {
        lastConsumed = s;
        curToken = new Token(i, s);
        consumed.setLength(0);
    }}
