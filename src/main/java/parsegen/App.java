package parsegen;

import gen.GrammarLexer;
import gen.GrammarParser;
import parsegen.generators.LexerGenerator;
import parsegen.generators.ParserGenerator;
import parsegen.model.LexRule;
import parsegen.model.SyntRule;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws GeneratorException {
        if (args.length != 3 || args[0] == null || args[1] == null || args[2] == null) {
            System.out.println("Usage: <grammar-file> <package-name> <location>");
        }
        try {
            String input = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
            Lexer lex = new GrammarLexer(CharStreams.fromString(input));
            GrammarParser parser = new GrammarParser(new CommonTokenStream(lex));
            GrammarListener listener = new GrammarListener();
            GrammarUtilities utilities = new GrammarUtilities();
            parser.addParseListener(listener);
            parser.gram();
            listener.checkGrammar();
            List<SyntRule> gr = listener.getGrammar().values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            utilities.check(gr);
            System.out.println("Grammar check OK");
            System.out.println(utilities.getFirstSet());
            System.out.println(utilities.getFollowSet());
            for (SyntRule rule : gr) {
                System.out.println(rule.getName() + " " + utilities.firstS(rule) + " " + rule.getDependencies());
            }
            List<LexRule> tokens = listener.getTokens();
            LexerGenerator lexGenerator = new LexerGenerator(Paths.get(args[2], "Lexer.java"), args[1]);
            lexGenerator.generate(tokens);
            ParserGenerator generator = new ParserGenerator(Paths.get(args[2]), args[1], utilities);
            generator.generate(tokens, listener.getGrammar(), listener.getAttrs(), listener.getContext());
            System.out.println("SUCCESS");
        } catch (IOException e) {
            System.err.println("Error while reading input: " + e.getMessage());
        }
    }
}
