package IntermediateCodeGeneration;

import SynatxAnalyzer.Symbol;

import java.io.*;
import java.util.*;

/**
 * Created by kodoo on 19.12.2015.
 */
public class IntermediateCodeGenerator {

    class RuleIdentity {
        RuleIdentity(Symbol keySymb, Symbol firstRuleSymbol) {
            this.keySymb = keySymb;
            this.firstRuleSymbol = firstRuleSymbol;
        }
        Symbol keySymb;
        Symbol firstRuleSymbol;
    }

    class Triada {
        String operation;
        String operand1;
        String operand2;

        @Override
        public String toString() {
            return "{" + operation + ", " + operand1 + ", " + operand2 + "}";
        }
    }

    private Map<RuleIdentity, List<Triada>> triadas;

    public IntermediateCodeGenerator(String relesFile, String charSet) throws IOException {
        triadas = new HashMap<>();
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

                System.out.printf("[%s, %s] -> {%s}\n", rule.keySymb, rule.firstRuleSymbol, ruleTriadas);
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

            // Левая часть правила вывода
            Symbol keySymb = new Symbol(scan.next());

            // Дальше должна быть стрелочка
            String arrow = scan.next();
            assert (arrow == ">");

            // Записываем правую часть вывода
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

            String operation = scan.next().substring(1);
            String operand1 = scan.next();
            String operand2 = scan.next();

            Triada tr = new Triada();
            tr.operation = operation;
            tr.operand1 = operand1;
            tr.operand2 = operand2;

            ruleTriadas.add(tr);

            //Если в конце стоит точка
            if (scan.hasNext())
                break;
        }

        return ruleTriadas;
    }

    public void generate(String outFile) {

    }

    public static void main(String[] args) throws IOException {

        IntermediateCodeGenerator generator = new IntermediateCodeGenerator("Files/rules.txt", "Cp1251");
    }
}
