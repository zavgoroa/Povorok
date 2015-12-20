package IntermediateCodeGeneration;

import LexicalAnalyzer.Analyzer;
import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenException;
import SynatxAnalyzer.Grammar;
import SynatxAnalyzer.Symbol;
import SynatxAnalyzer.SyntaxException;
import SynatxAnalyzer.SyntaxTree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

/**
 * Created by kodoo on 19.12.2015.
 */
@SuppressWarnings("SpellCheckingInspection")
public class IntermediateCodeGenerator {

    private static int generatorCount = 0;
    PrintWriter out;

    class RuleIdentity  {
        RuleIdentity(Symbol keySymb, Symbol firstRuleSymbol) {
            this.keySymb = keySymb;
            this.firstRuleSymbol = firstRuleSymbol;
        }
        Symbol keySymb;
        Symbol firstRuleSymbol;

        @Override
        public boolean equals(Object obj) {
            return toString().equals(obj.toString());
        }

        @Override
        public String toString() {
            return keySymb.toStringClear() + " " + firstRuleSymbol.toStringClear();
        }
    }

    class Triada {
        String operation;
        String operand1;
        String operand2;

        boolean returnes;

        @Override
        public String toString() {
            if (returnes)
                return "{" + operation + ", " + operand1 + ", " + operand2 + " =>}";

            return "{" + operation + ", " + operand1 + ", " + operand2 + "}";
        }
    }

    private Map<RuleIdentity, List<Triada>> triadas;

    public IntermediateCodeGenerator(String relesFile, String charSet, PrintWriter out) throws IOException {
        this.out = out;

        triadas = new TreeMap<>((o1, o2) -> {
            RuleIdentity first = o1;
            RuleIdentity second = o2;

            return first.toString().compareTo(second.toString());
        });
        parseRules(relesFile, charSet);
    }

    private void parseRules(String fileName, String charSet) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileName), charSet))) {

            RuleIdentity rule;
            do {

                rule = findRule(br);
                List<Triada> ruleTriadas = findTriadas(br);

                if (rule == null)
                    break;

                System.out.printf("%s -> {%s}\n", rule, ruleTriadas);
                triadas.put(rule, ruleTriadas);
            } while (rule != null);
        }
    }

    private RuleIdentity findRule(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            Scanner scan = new Scanner(line);

            if (!scan.hasNext())
                continue;

            // ����� ����� ������� ������
            Symbol keySymb = new Symbol(scan.next());

            // ������ ������ ���� ���������
            String arrow = scan.next();
            assert (arrow == ">");

            // ���������� ������ ����� ������
            Symbol firstRule = new Symbol(scan.next());

            return new RuleIdentity(keySymb, firstRule);
        }

        return null;
    }

    private List<Triada> findTriadas(BufferedReader br) throws IOException {
        String line;
        List<Triada> ruleTriadas = new LinkedList<>();
        while ((line = br.readLine()) != null) {
            Scanner scan = new Scanner(line);

            if (!scan.hasNext())
                continue;

            String operation;
            boolean returns = false;
            if (line.charAt(1) == '>') {
                returns = true;
                operation = scan.next().substring(2);
            }
            else
                operation = scan.next().substring(1);

            String operand1 = scan.next();
            String operand2 = scan.next();

            Triada tr = new Triada();
            tr.operation = operation;
            tr.operand1 = operand1;
            tr.operand2 = operand2;
            tr.returnes = returns;

            ruleTriadas.add(tr);

            //���� � ����� ����� �����
            if (scan.hasNext())
                break;
        }

        return ruleTriadas;
    }

    public void generate(String outFile, SyntaxTree tree) {

    }

    /**
     * @param curNode
     * @return last triada result variable name
     */
    String processNode(DefaultMutableTreeNode curNode, String outLabel) {

        StringBuilder outBuffer = new StringBuilder();
        LabelGenerator lGen = new LabelGenerator(generateGeneratorId());

        Symbol curNodeValue = (Symbol) curNode.getUserObject();
        Enumeration<DefaultMutableTreeNode> children = curNode.children();
        Symbol firstChildValue = (Symbol) children.nextElement().getUserObject();

        RuleIdentity t = new RuleIdentity(curNodeValue, firstChildValue);
        //System.out.printf("Try to find by: %s\n", t);
        List<Triada> ruleTriadas = triadas.get(t);

        if (ruleTriadas == null) {
            System.out.println("Cant find in map\n");
        }

        String returnVar = "*";
        for (Triada tr : ruleTriadas) {
            outBuffer.append(tr.operation + " ");

            String operand1 = processOperand(tr.operand1, curNode.children(), lGen, outLabel);
            outBuffer.append(operand1 + " ");

            String operand2;
            if (tr.operation.equals("_") && !tr.operand1.equals("_"))
                operand2 = processOperand(tr.operand2, curNode.children(), lGen, operand1);
            else
                operand2 = processOperand(tr.operand2, curNode.children(), lGen, outLabel);

            outBuffer.append(operand2 + " ");

            if (tr.returnes) {
                returnVar = varGenerator.getVarName();
                outBuffer.append(returnVar);
            }
            else
                outBuffer.append("_");

            if (!tr.operation.equals("_"))
                out.println(outBuffer);

            outBuffer.setLength(0);
        }

        return returnVar;
    }

    String processOperand(String operand, Enumeration<DefaultMutableTreeNode> children, LabelGenerator lGen, String outLabel) {
        if (operand.charAt(0) == 'c') {
            //����� ������
            int childNum = Integer.parseInt(operand.substring(1));

            int i = 0;
            DefaultMutableTreeNode child = null;
            while (children.hasMoreElements()) {
                child = children.nextElement();

                if (i == childNum)
                    break;
                i++;
            }

            Symbol childNode = (Symbol) child.getUserObject();

            if (childNode.isTerminal())
                if ((childNode.getType().equals(Token.TokensType.ID.toString()))
                    ||(childNode.getType().equals(Token.TokensType.CI.toString())))
                    return childNode.getValue();
                else
                    return childNode.getType();
            else {
                return processNode(child, outLabel);
            }
        }
        else if (operand.charAt(0) == 'l') {

            int labelNum = Integer.parseInt(operand.substring(1));
            return lGen.getLabel(labelNum);
        }
        else if (operand.charAt(0) == 'o') { //out
            return outLabel;
        }

        return operand;
    }


    public static void main(String[] args) throws IOException, TokenException {


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

        try (PrintWriter writer = new PrintWriter(new FileWriter("Files/intercode.obj"), true)) {
        IntermediateCodeGenerator generator =
                new IntermediateCodeGenerator("Files/rules.txt", "Cp1251", writer);
        generator.processNode(gr.getTree().getRoot(), "L_ENDPGM");
        }
    }

    class LabelGenerator {
        private final int id;

        LabelGenerator(int id) {
            this.id = id;
        }

        public String getLabel(int label) {
            return "L_" + id + "_" + label;
        }
    }

    static int generateGeneratorId() {
        return generatorCount++;
    }

    static class varGenerator {
        static int counter = 0;

        public static String getVarName() {
            return "t" + counter++;
        }

    }
}
