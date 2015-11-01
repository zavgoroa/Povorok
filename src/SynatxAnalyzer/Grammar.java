package SynatxAnalyzer;

import java.io.*;
import java.util.*;

/**
 * Created by kodoo on 01.11.2015.
 */
public class Grammar {

    Map<Symbol, Set<List<Symbol>>> grammaTable;

    public Grammar() {
        grammaTable = new TreeMap<>((o1, o2) -> {
            return o1.getValue().compareTo(o2.getValue());
        });
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

                // ����� ����� ������� ������
                Symbol keySymb = new Symbol(scan.next());

                // ������ ������ ���� ���������
                String arrow = scan.next();
                assert(arrow == ">");

                // ���������� ������ ����� ������ � ���� ������ ��������
                List<Symbol> ruleList = new LinkedList<>();
                while (scan.hasNext()) {
                    ruleList.add(new Symbol(scan.next()));
                }

                // ���� ����� ����� � ������� ���
                if (!grammaTable.containsKey(keySymb)) {
                    grammaTable.put(keySymb, new HashSet<>());
                }

                grammaTable.get(keySymb).add(ruleList);
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

    public static void main(String[] args) {

        Grammar gr = new Grammar();
        try {
            gr.loadFromFile("Files/rules.txt", "Cp1251");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(gr);
    }
}
