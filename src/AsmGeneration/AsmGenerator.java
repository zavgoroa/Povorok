package AsmGeneration;

import java.io.*;
import java.util.*;

/**
 * Created by kodoo on 20.12.2015.
 */
public class AsmGenerator {

    AsmGenerator(String fileIn, String charSet) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileIn), charSet))) {

            String line;
            while ((line = br.readLine()) != null) {
                Scanner scan = new Scanner(line);

                if (!scan.hasNext())
                    continue;

                byte[] ba = "А примерчик можно?".getBytes("koi8-r");

                for (int i = 0; i < ba.length; i++) {
                    ba[i] = (byte) (ba[i] & 127);
                }

                System.out.println(new String(ba));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        neRusskiy.loadChars();
        AsmGenerator asm = new AsmGenerator("Files/intercode.obj", "Cp1251");
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

                    System.out.printf("%s/%s ё-> %s\n", capital, minuscule, nerusChars);
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
                neRusStr.append(nerusChar);
            }

            return neRusStr.toString();
        }
    }
}
