package AsmGeneration;

import IntermediateCodeGeneration.IntermediateCodeGenerator;
import LexicalAnalyzer.Analyzer;
import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenException;
import SynatxAnalyzer.Grammar;
import SynatxAnalyzer.SyntaxException;

import java.io.*;
import java.util.*;

/**
 * Created by kodoo on 20.12.2015.
 */
public class AsmGenerator {

    private Map<String, String> asmRules;

    AsmGenerator(String fileIn, String charSet) throws IOException {
        asmRules = new HashMap<>();
        loadAsmRules(fileIn, charSet);
    }

    public void parsInterCode(String fileName, String charSet, Analyzer anal) throws IOException {
        StringBuilder outAsm = new StringBuilder();
        int sasha = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileName), charSet))) {
            prefix(outAsm, anal);
            String line;
            while ((line = br.readLine()) != null) {
                Scanner scan = new Scanner(line);

                if (!scan.hasNext())
                    continue;

                String operation = scan.next();
                String operand1 = scan.next();
                String operand2 = scan.next();
                String resultvar = scan.next();

                outAsm.append(genAsm(operation, operand1, operand2, resultvar, sasha++));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        postfix(outAsm);

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName + ".ASM"), true)) {
            writer.println(neRusskiy.neRusString(outAsm.toString()));
        }

    }

    private void postfix(StringBuilder outAsm) {
        outAsm.append("ret\n" +
                "\t\tmain endp\n" +
                "\tcode ends\n" +
                "\tend main\n");
    }

    private void prefix(StringBuilder outAsm, Analyzer anal) throws IOException {
        outAsm.append("" +
                ";=============================================================================\t\n" +
                "\tastack segment stack\n" +
                "\t\tdw 500 dup(?)\n" +
                "\tastack ends\n" +
                "                       \n" +
                ";=============================================================================\n" +
                "\tdata segment\n" +
                "\t      \tbuf        dw 16 dup(?) \n");

        for (String id : anal.getListId()) {
            outAsm.append(neRusskiy.neRusString(id));
            outAsm.append(" DW   0\n");
        }

        for (int i = 0; i < IntermediateCodeGenerator.generatorCount; i++) {
            outAsm.append("t");
            outAsm.append(i);
            outAsm.append(" DW   0\n");
        }

        outAsm.append("data ends" +
                "\t\t");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("Files/asmHeader.asm")))) {
            String line;
            while ((line = br.readLine()) != null) {
                outAsm.append(line);
                outAsm.append("\n");
            }
        }
    }

    public String genAsm(String op, String arg1, String arg2, String res, int indexCount) {

        String asmRule = asmRules.get(op);
        asmRule = asmRule.replaceAll("\\$1", arg1);
        asmRule = asmRule.replaceAll("\\$2", arg2);
        asmRule = asmRule.replaceAll("\\$3", res);

        asmRule = asmRule.replace("#1", "LABL@" + indexCount);
        asmRule = asmRule.replace("#2", "LABLA@" + indexCount);
        asmRule = asmRule.replace("#3", "LABLB@" + indexCount);

        return asmRule;
    }

    void loadAsmRules(String fileIn, String charSet) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileIn), charSet))) {

            String line;
            while ((line = br.readLine()) != null) {
                Scanner scan = new Scanner(line);

                if (!scan.hasNext())
                    continue;


                assert(line.charAt(0) == '>');
                String operation = scan.next().substring(1);
                System.out.println("Operation: " + operation);

                StringBuilder asmRule = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    if (line.indexOf(".") != -1)
                        break;

                    asmRule.append(line);
                    asmRule.append("\n");
                }

                System.out.printf("rule: %s -> %s\n", operation, asmRule.toString());
                asmRules.put(operation, asmRule.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, TokenException {

        String sourceFile = args[0];

        FileInputStream fileStream = new FileInputStream(sourceFile);
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

        try (PrintWriter writer = new PrintWriter(new FileWriter(sourceFile + ".obj"), true)) {
            IntermediateCodeGenerator generator =
                    new IntermediateCodeGenerator("Files/rules.txt", "Cp1251", writer);
            generator.processNode(gr.getTree().getRoot(), "L_ENDPGM");
        }

        neRusskiy.loadChars();
        AsmGenerator asm = new AsmGenerator("Files/toAsm.txt", "Cp1251");
        asm.parsInterCode(sourceFile + ".obj", "Cp1251", analyzer);
    }


    static class neRusskiy {
        static public Map<Character, String> neRus = new HashMap<>();

        static void loadChars() {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("Files/nonRus.txt")))) {

                String line;
                while ((line = br.readLine()) != null) {
                    Scanner scan = new Scanner(line);

                    if (!scan.hasNext())
                        continue;

                    String rusChars = scan.next();
                    char capital = rusChars.charAt(0);
                    char minuscule = rusChars.charAt(1);

                    String nerusChars = scan.next();

                    //System.out.printf("%s/%s ¸-> %s\n", capital, minuscule, nerusChars);
                    neRus.put(capital, nerusChars);
                    neRus.put(minuscule, nerusChars);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String neRusString(String rusString) {
            StringBuilder neRusStr = new StringBuilder();
            for (char rusCh : rusString.toCharArray()) {

                if (rusCh == ' ') {
                    neRusStr.append(" ");
                    continue;
                }

                String nerusChar = neRus.get(rusCh);

                if (nerusChar == null)
                    nerusChar = "" + rusCh;

                neRusStr.append(nerusChar);
            }

            return neRusStr.toString();
        }
    }
}
