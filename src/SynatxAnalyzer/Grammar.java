package SynatxAnalyzer;

import LexicalAnalyzer.Analyzer;
import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenException;

import java.io.*;
import java.util.*;

/**
 * Created by kodoo on 01.11.2015.
 */
public class Grammar {

    public final static Symbol startSymbol = new Symbol("Recipe");

    private Map<Symbol, Set<List<Symbol>>> grammaTable;
    private Map<Symbol, Set<Token.TokensType>> firsts;

    public Grammar() {
        grammaTable = new TreeMap<>((o1, o2) -> {
            return o1.getValue().compareTo(o2.getValue());
        });

        firsts = new HashMap<>();
    }

    public void loadFromFile(String fileIn, String charSet) throws IOException {

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileIn), charSet))) {

            String line;
            while ((line = br.readLine()) != null) {
                Scanner scan = new Scanner(line);

                if (!scan.hasNext())
                    continue;

                // Левая часть правила вывода
                Symbol keySymb = new Symbol(scan.next());

                // Дальше должна быть стрелочка
                String arrow = scan.next();
                assert(arrow == ">");

                // Записываем правую часть вывода в виде списка символов
                List<Symbol> ruleList = new LinkedList<>();
                while (scan.hasNext()) {
                    ruleList.add(new Symbol(scan.next()));
                }

                // Если левой части в таблице нет
                if (!grammaTable.containsKey(keySymb)) {
                    grammaTable.put(keySymb, new HashSet<>());
                }

                grammaTable.get(keySymb).add(ruleList);
            }
        }
    }

    Set<Token.TokensType> getFirst(Symbol symbol) {

        Set<List<Symbol>> setOfRules = grammaTable.get(symbol);
        Set<Token.TokensType> firsts = new HashSet<>();

        for (List<Symbol> rule : setOfRules) {

            Symbol leftSymbol = rule.get(0);

            if (leftSymbol.isTerminal()) {
                firsts.add(Token.TokensType.valueOf(leftSymbol.getValue()));
            }
            else {
                firsts.addAll(getFirst(leftSymbol));
            }
        }

        return firsts;
    }

    private void initFirsts() {

        for (Symbol nonTerm : grammaTable.keySet()) {
            firsts.put(nonTerm, getFirst(nonTerm));
        }

    }

    List<Symbol> getRule(Symbol a, Token first) {

        Set<List<Symbol>> rules = grammaTable.get(a);

        for (List<Symbol> r : rules) {
            Set<Token.TokensType> firsts = getFirst(r.get(0));
            if (firsts.contains(first.getTokenType()))
                return r;
        }

        return null;
    }

    public void checkSyntax(Stack<Token> tokens) {

        Stack<Symbol> st = new Stack<>();
        st.push(startSymbol);

        while (!st.isEmpty()) {

            Symbol a = st.peek();
            Token token = null;

            if (tokens.isEmpty())
                token = new Token(Token.TokensType.e);
            else
                token = tokens.peek();

            System.out.printf("st:%s token:%s\n", a.getValue(), token.getTokenType());

            if (a.getValue() == token.getTokenType().toString()) {
                // ВЫБРОС
                System.out.printf("ВЫБРОС\n");
                tokens.pop();
                continue;
            }

            List<Symbol> rule = getRule(a, token);
            if (rule == null) {
                // ОШИБКА
                System.out.printf("ОШИБКА\n");
                return;
            }

            Collections.reverse(rule);
            for (Symbol symbol : rule) {
                st.push(symbol);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.UK);

        formatter.format("Rules:\n");
        for (Symbol key : grammaTable.keySet()) {
            formatter.format("%s -> \n", key);

            for (List<Symbol> list : grammaTable.get(key))
                formatter.format("  %s\n", list);
        }

        formatter.format("FIRST:\n");
        for (Symbol nonTerm : firsts.keySet()) {
            formatter.format("FIRST(%s) = [", nonTerm.getValue());

            for (Token.TokensType first : firsts.get(nonTerm)) {
                formatter.format("%s ", first);
            }
            formatter.format("]\n");
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException, TokenException {

        Grammar gr = new Grammar();
        try {
            gr.loadFromFile("Files/rules.txt", "Cp1251");
        } catch (IOException e) {
            e.printStackTrace();
        }
        gr.initFirsts();
        System.out.print(gr);

//        for (Symbol s : gr.getRule(startSymbol, new Token(Token.TokensType.ID))) {
//            System.out.printf("%s ", s);
//        }

        FileInputStream fileStream = new FileInputStream("Files/source.chef");
        Analyzer analyzer = new Analyzer(fileStream);

        Stack<Token> st = new Stack<>();
        st.addAll(analyzer.getListTokens());
        Collections.reverse(st);
        gr.checkSyntax(st);

    }


}
