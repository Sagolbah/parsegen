package examples.cpp;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Lexer lex;

     public MainNode parse(String text) {
        lex = new Lexer();
        lex.setExpr(text);
        lex.nextToken();
        return main();
    }

    public Token ID() {
        Token t = lex.getCurToken();
        if (t.getType() != 0)
            throw new IllegalArgumentException("Expected " + t.getType());
        lex.nextToken();
        return t;
    }

    public Token SEMICOLON() {
        Token t = lex.getCurToken();
        if (t.getType() != 1)
            throw new IllegalArgumentException("Expected " + t.getType());
        lex.nextToken();
        return t;
    }

    public Token COMMA() {
        Token t = lex.getCurToken();
        if (t.getType() != 2)
            throw new IllegalArgumentException("Expected " + t.getType());
        lex.nextToken();
        return t;
    }

    public Token ASTERISK() {
        Token t = lex.getCurToken();
        if (t.getType() != 3)
            throw new IllegalArgumentException("Expected " + t.getType());
        lex.nextToken();
        return t;
    }

    public Token AMPERSAND() {
        Token t = lex.getCurToken();
        if (t.getType() != 4)
            throw new IllegalArgumentException("Expected " + t.getType());
        lex.nextToken();
        return t;
    }

    public MainNode main() {
        List<Node> children = new ArrayList<>();
        switch (lex.getCurToken().getType()) {
			case 5:
			{
				MainNode result = new MainNode();
				System.out.println("Main EPS");
				result.setChildren(children);
				return result;
			}
			case 0:
			{
				MainNode result = new MainNode();
				children.add(desc());
				children.add(SEMICOLON());
				children.add(main());
				System.out.println("Main");
				result.setChildren(children);
				return result;
			}
			default:
				throw new IllegalArgumentException("Unexpected token: " + lex.getCurToken().getName());
		}
	}
    public DescNode desc() {
        List<Node> children = new ArrayList<>();
        switch (lex.getCurToken().getType()) {
			case 0:
			{
				DescNode result = new DescNode();
				children.add(type());
				children.add(var());
				children.add(next());
				System.out.println("Desc");
				result.setChildren(children);
				return result;
			}
			default:
				throw new IllegalArgumentException("Unexpected token: " + lex.getCurToken().getName());
		}
	}
    public NextNode next() {
        List<Node> children = new ArrayList<>();
        switch (lex.getCurToken().getType()) {
			case 1:
			{
				NextNode result = new NextNode();
				System.out.println("Next EPS");
				result.setChildren(children);
				return result;
			}
			case 2:
			{
				NextNode result = new NextNode();
				children.add(COMMA());
				children.add(var());
				children.add(next());
				System.out.println("Next");
				result.setChildren(children);
				return result;
			}
			default:
				throw new IllegalArgumentException("Unexpected token: " + lex.getCurToken().getName());
		}
	}
    public TypeNode type() {
        List<Node> children = new ArrayList<>();
        switch (lex.getCurToken().getType()) {
			case 0:
			{
				TypeNode result = new TypeNode();
				children.add(ID());
				System.out.println(children.get(0).getName());
				result.setChildren(children);
				return result;
			}
			default:
				throw new IllegalArgumentException("Unexpected token: " + lex.getCurToken().getName());
		}
	}
    public VarNode var() {
        List<Node> children = new ArrayList<>();
        switch (lex.getCurToken().getType()) {
			case 3:
			{
				VarNode result = new VarNode();
				children.add(ASTERISK());
				children.add(var());
				System.out.println("Asterisk var");
				result.setChildren(children);
				return result;
			}
			case 4:
			case 0:
			{
				VarNode result = new VarNode();
				children.add(var2());
				System.out.println(children.get(0).getName());
				result.setChildren(children);
				return result;
			}
			default:
				throw new IllegalArgumentException("Unexpected token: " + lex.getCurToken().getName());
		}
	}
    public Var2Node var2() {
        List<Node> children = new ArrayList<>();
        switch (lex.getCurToken().getType()) {
			case 0:
			{
				Var2Node result = new Var2Node();
				children.add(ID());
				System.out.println(children.get(0).getName());
				result.setChildren(children);
				return result;
			}
			case 4:
			{
				Var2Node result = new Var2Node();
				children.add(AMPERSAND());
				children.add(ID());
				System.out.println("Ampersand " + children.get(1).getName());
				result.setChildren(children);
				return result;
			}
			default:
				throw new IllegalArgumentException("Unexpected token: " + lex.getCurToken().getName());
		}
	}
}
