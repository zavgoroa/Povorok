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

    private SyntaxTree tree;

    Analyzer anal;

    public Grammar(Analyzer anal) {

        this.anal = anal;

        grammaTable = new TreeMap<>((o1, o2) -> {
            return o1.getValue().compareTo(o2.getValue());
        });

        firsts = new TreeMap<>((o1, o2) -> {
            return o1.getValue().compareTo(o2.getValue());
        });

        tree = new SyntaxTree(startSymbol);
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
                System.out.printf("%s -> %s\n", keySymb, ruleList);
            }
        }
    }

    Set<Token.TokensType> getFirst(Symbol symbol) {

        Set<List<Symbol>> setOfRules = grammaTable.get(symbol);
        Set<Token.TokensType> firsts = new TreeSet<>((o1, o2) -> {
            return o1.toString().compareTo(o2.toString());
        });

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
        System.out.printf("Get rule %s -> FIRST(%s)\n", a.getValue(), first.getTokenType());

        if (rules == null)
            return null;

        for (List<Symbol> r : rules) {
            System.out.printf("     %s -> %s\n", a.getValue(), r);
            System.out.printf("     FIRST(%s) = %s\n", r.get(0), firsts.get(r.get(0)));

            Symbol left = r.get(0);

            if (left.getValue().equals(Token.TokensType.e.toString())) {
                System.out.printf("     OK(empty rule)\n");
                return r;
            }


            if (left.isTerminal()) {
                if (first.getTokenType().toString().equals(left.getValue())) {
                    System.out.printf("     OK(terminal)\n");
                    return r;
                }
            }
            else if (firsts.get(left).contains(first.getTokenType())) {
                System.out.printf("     OK\n");
                return r;
            }

            System.out.printf("     NO\n");
        }


        return null;
    }

    public void checkSyntax(Stack<Token> tokens) throws SyntaxException {

        Stack<Symbol> st = new Stack<>();
        st.push(startSymbol);

        while (!st.isEmpty()) {

            Symbol a = st.pop();
            Token token = null;

            if (tokens.isEmpty())
                token = new Token(Token.TokensType.e);
            else
                token = tokens.peek();

            System.out.printf("st:%s token:%s(%s)\n", a.getValue(), token.getTokenType(), token.getIndex());

            if (a.getValue().equals(token.getTokenType().toString())) {
                // ВЫБРОС
                System.out.printf("POP\n");
                tokens.pop();
                tree.next();
                continue;
            }

            List<Symbol> rule = getRule(a, token);
            if (rule == null) {
                // ОШИБКА
                throw new SyntaxException(a, token);
            }

            tree.addChildren(rule);

            for (int i = rule.size() - 1; i >= 0; i--) {
                Symbol s = rule.get(i);
                if (!s.getValue().equals(Token.TokensType.e.toString()))
                    st.push(rule.get(i));
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

    public String formatTree() {
        return tree.toString();
    }

    public static void main(String[] args) throws IOException, TokenException {



//        for (Symbol s : gr.getRule(startSymbol, new Token(Token.TokensType.ID))) {
//            System.out.printf("%s ", s);
//        }

        FileInputStream fileStream = new FileInputStream("Files/source.chef");
        Analyzer analyzer = new Analyzer();

        if (!analyzer.parcingText(fileStream)) return;

		analyzer.showResultAnalyzer();
        Stack<Token> st = new Stack<>();
        st.addAll(analyzer.getListTokens());
        Collections.reverse(st);


        Grammar gr = new Grammar(analyzer);
        try {
            gr.loadFromFile("Files/rules.txt", "Cp1251");
        } catch (IOException e) {
            e.printStackTrace();
        }
        gr.initFirsts();
        System.out.print(gr);

        try {
            gr.checkSyntax(st);
        } catch (SyntaxException e) {

            StringBuilder str = new StringBuilder();
            str.append("Syntax Error! Imposable to resolve [");
            str.append(e.a);
            str.append("] to [");

            if (e.t.getTokenType() == Token.TokensType.ID) {
                str.append(analyzer.getListId().get(e.t.getIndex()));
            }
            else if (e.t.getTokenType() == Token.TokensType.CI) {
                str.append(analyzer.getListConstanst().get(e.t.getIndex()));
            }
            else {
                str.append(e.t.getTokenType().toString());
            }

            str.append("]!");

            System.out.println(str);
            return;
        }



        System.out.println(gr.formatTree());

    }


}
