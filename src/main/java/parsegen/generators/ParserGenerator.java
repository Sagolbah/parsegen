package parsegen.generators;

import parsegen.GrammarUtilities;
import parsegen.model.LexRule;
import parsegen.model.SyntRule;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ParserGenerator {
    private Path rootDir;
    private String pkgName;  // Can be null if pkg not needed
    private GrammarUtilities utilities;

    public ParserGenerator(final Path rootDir, final String pkgName, GrammarUtilities utilities) {
        this.rootDir = rootDir;
        this.pkgName = pkgName;
        this.utilities = utilities;
    }

    public void generate(final List<LexRule> tokens, final Map<String, List<SyntRule>> grammar, final List<String> attrs, final List<String> ctx) {
        generateDefaults(attrs);
        generateNodes(new ArrayList<>(grammar.keySet()));
        generateParser(tokens, grammar, ctx);
    }

    private void generateParser(final List<LexRule> tokens, final Map<String, List<SyntRule>> grammar, final List<String> ctx) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(String.valueOf(rootDir), "Parser.java"))) {
            generateParserIntro(writer, ctx);
            generateTerminalFunc(writer, tokens);
            generateRC(writer, tokens, grammar);
            writer.write("}" + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error while generating Parser: " + e.getMessage());
        }
    }

    private void generateRC(final BufferedWriter writer, final List<LexRule> tokens, final Map<String, List<SyntRule>> grammar) throws IOException {
        Map<String, Integer> termToID = new HashMap<>();
        for (int i = 0; i < tokens.size(); i++) {
            termToID.put(tokens.get(i).getName(), i);
        }
        termToID.put("END", tokens.size());
        for (Map.Entry<String, List<SyntRule>> nt : grammar.entrySet()) {
            final String name = capitalize(nt.getKey()) + "Node";
            writer.write(String.format("    public %s %s() {\n" +
                    "        List<Node> children = new ArrayList<>();\n" +
                    "        switch (lex.getCurToken().getType()) {\n", name, nt.getKey()));
            for (SyntRule rule : nt.getValue()) {
                Set<String> first = utilities.firstS(rule);
                for (String x : first) {
                    writer.write("\t\t\tcase " + termToID.get(x) + ":");
                    writer.write(System.lineSeparator());
                }
                writer.write("\t\t\t{" + System.lineSeparator());
                writer.write(String.format("\t\t\t\t%s result = new %s();" + System.lineSeparator(), name, name));
                List<String> deps = rule.getDependencies();
                List<String> actions = rule.getActions();
                for (int i = 0; i < deps.size(); i++) {
                    String dep = deps.get(i);
                    if (!dep.equals("$EPS")) {
                        writer.write("\t\t\t\tchildren.add(" + dep + "());" + System.lineSeparator());
                    }
                    if (actions.get(i) != null) {
                        writer.write("\t\t\t\t" + actions.get(i) + System.lineSeparator());
                    }
                }
                writer.write("\t\t\t\tresult.setChildren(children);" + System.lineSeparator());
                writer.write("\t\t\t\treturn result;" + System.lineSeparator() + "\t\t\t}" + System.lineSeparator());
            }
            writer.write("\t\t\tdefault:" + System.lineSeparator());
            writer.write("\t\t\t\tthrow new IllegalArgumentException(\"Unexpected token: \" + lex.getCurToken().getName());" + System.lineSeparator());
            writer.write("\t\t}" + System.lineSeparator());
            writer.write("\t}" + System.lineSeparator());
        }
    }

    private void generateTerminalFunc(final BufferedWriter writer, final List<LexRule> tokens) throws IOException {
        for (int i = 0; i < tokens.size(); i++) {
            writer.write(String.format("" +
                    "    public Token %s() {\n" +
                    "        Token t = lex.getCurToken();\n" +
                    "        if (t.getType() != %d)\n" +
                    "            throw new IllegalArgumentException(\"Expected \" + t.getType());\n" +
                    "        lex.nextToken();\n" +
                    "        return t;\n" +
                    "    }\n\n", tokens.get(i).getName(), i));
        }
    }

    private void generateParserIntro(final BufferedWriter writer, final List<String> ctx) throws IOException {
        writePkg(writer);
        writer.write("import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class Parser {\n" +
                "    private Lexer lex;\n\n ");
        for (String s : ctx) {
            writer.write("\t" + s + "\n");
        }
        writer.write("    public MainNode parse(String text) {\n" +
                "        lex = new Lexer();\n" +
                "        lex.setExpr(text);\n" +
                "        lex.nextToken();\n" +
                "        return main();\n" +
                "    }\n\n");
    }

    private void writePkg(final BufferedWriter writer) throws IOException {
        if (pkgName != null) {
            writer.write("package " + pkgName + ";" + System.lineSeparator());
        }
    }

    private void generateDefaults(List<String> attrs) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(String.valueOf(rootDir), "Token.java"))) {
            writePkg(writer);
            writer.write(
                    "\n" +
                            "import java.util.List;\n" +
                            "import java.util.ArrayList;\n" +
                            "\n" +
                            "public class Token extends Node {\n" +
                            "    private int type;\n" +
                            "    private String name;\n" +
                            "\n" +
                            "    public int getType() {\n" +
                            "        return type;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getName() {\n" +
                            "        return name;\n" +
                            "    }\n" +
                            "\n" +
                            "    public Token(int type, String text) {\n" +
                            "        this.type = type;\n" +
                            "        this.name = text;\n" +
                            "    }\n" +
                            "\n" +
                            "    @Override\n" +
                            "    public List<Node> getChildren() {\n" +
                            "        return new ArrayList<>();\n" +
                            "    }\n" +
                            "\n" +
                            "}\n");
        } catch (IOException e) {
            System.err.println("Error while creating Token: " + e.getMessage());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(String.valueOf(rootDir), "Node.java"))) {
            writePkg(writer);
            writer.write(
                    "\n" +
                            "import java.util.List;\n" +
                            "\n" +
                            "public abstract class Node {\n");
            writer.write("protected List<Node> children;\n");
            for (String attr : attrs) {
                writer.write("\tpublic " + attr + System.lineSeparator());
            }
            writer.write(
                    "    public List<Node> getChildren() {\n" +
                            "        return children;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setChildren(List<Node> children) {\n" +
                            "        this.children = children;\n" +
                            "    }\n" +
                            "\n" +
                            "    public abstract String getName();\n" +
                            "}");
        } catch (IOException e) {
            System.err.println("Error while creating Node: " + e.getMessage());
        }
    }


    private void generateNodes(final List<String> nonterms) {
        for (final String nt : nonterms) {
            final String name = capitalize(nt) + "Node";
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(String.valueOf(rootDir), name + ".java"))) {
                writePkg(writer);
                writer.write(
                        "import java.util.List;\n" +
                                "\n" +
                                "public class " + name + " extends Node {\n" +
                                "\n" +
                                "    @Override\n" +
                                "    public String getName() {\n" +
                                "        return \"" + nt + "\"; // <GEN>\n" +
                                "    }\n" +
                                "}\n");
            } catch (IOException e) {
                System.err.println("Error while creating node file for " + nt + " " + e.getMessage());
            }
        }
    }

    private String capitalize(String str) {
        if (str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
