package SynatxAnalyzer;

import java.io.*;
import java.util.*;

/**
 * Created by kodoo on 01.11.2015.
 */
public class Grammar {
    Map<Symbol, Set<List<Symbol>>> grammaTable;

    public Grammar() {
        grammaTable = new LinkedHashMap<>();
    }

    public Map<Symbol, Set<List<Symbol>>> getGrammar() {
        return grammaTable;
    }
    
    public void loadFromFile(String fileIn, String charSet) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileIn), charSet))) {

            String line;
            while ((line = br.readLine()) != null) {
                Scanner scan = new Scanner(line);

                if (!scan.hasNext()) {
                    continue;
                }

                Symbol keySymb = new Symbol(scan.next());

                String arrow = scan.next();
                assert(arrow == ">");

                List<Symbol> ruleList = new LinkedList<>();
                while (scan.hasNext()) {
                    ruleList.add(new Symbol(scan.next()));
                }
                
                boolean flag = false;
                Symbol saveFoundSymbol = null;
                for (Symbol sym : grammaTable.keySet()) {
                    if (sym.getValue().equals(keySymb.getValue())) {
                        flag = true;
                        saveFoundSymbol = sym;
                        break;
                    }
                }

                if (!flag) {
                    grammaTable.put(keySymb, new LinkedHashSet<>());
                    grammaTable.get(keySymb).add(ruleList);
                } else {
                    if (saveFoundSymbol != null) {
                        grammaTable.get(saveFoundSymbol).add(ruleList);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.UK);

        for (Symbol key : grammaTable.keySet()) {
            formatter.format("%s -> \n", key);

            for (List<Symbol> list : grammaTable.get(key))
                formatter.format("  %s\n", list);
        }

        return sb.toString();
    }
}
