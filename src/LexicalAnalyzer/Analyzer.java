package LexicalAnalyzer;

import java.io.*;
import java.util.*;

/**
 * Created by kodoo on 09.10.2015.
 */
class Analyzer {

    Map<String, ProcessorSequenceWord> mapProcessSeqWord;
    ArrayList<Token> listTokens;
    ArrayList<Double> listConstans;
    ArrayList<String> listId;

    public Analyzer(FileInputStream fileStream) throws TokenException, IOException {
        mapProcessSeqWord = new HashMap<>();
        listTokens = new ArrayList<>();
        listId = new ArrayList<>();
        listConstans = new ArrayList<>();
        initMapKeyWord();

        BufferedInputStream inputStream = new BufferedInputStream(fileStream);
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        scanner.useDelimiter("[\\r\\n\\.]+");

        String saveReadWord = "";
        while (scanner.hasNext()) {
            String row = scanner.next();
            row = row.trim();
            String[] words = row.split("[ \\t]+");

            for (int i = 0; i < words.length; ++i) {

                if (isNumeric(words[i])) {
                    saveReadWord = addToken(saveReadWord.trim().toLowerCase());
                    addConstants(words[i]);
                    continue;
                }

                ProcessorSequenceWord keyWordProcess = mapProcessSeqWord.get(words[i]);
                if (keyWordProcess == null) {
                    int index = listId.indexOf(words[i].trim());
                    if (index < 0) {
                        saveReadWord += words[i].trim() + " ";
                    } else {
                        saveReadWord = addToken(words[i].trim().toLowerCase());
                    }
                } else {
                    Token token = keyWordProcess.processSequence(words, i);
                    i += keyWordProcess.sizeSequenceWord() - 1;
                    listTokens.add(token);
                }
            }
            saveReadWord = addToken(saveReadWord.trim().toLowerCase());
            listTokens.add(new Token(Token.TokensType.SRPT, -1));
        }
    }

    private String addToken(String id) {
        if (!id.isEmpty()) {
            int index = listId.indexOf(id);
            if (index < 0) {
                listId.add(id);
                listTokens.add(new Token(Token.TokensType.ID, listId.size() - 1));
            } else {
                listTokens.add(new Token(Token.TokensType.ID, index));
            }
        }
        return "";
    }

    private void addConstants(String word) {
        Double value = Double.parseDouble(word);
        listConstans.add(value);
        listTokens.add(new Token(Token.TokensType.CI, listId.size() - 1));
    }

    private boolean isNumeric(String word) {
        return word.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean haveDotOnEnd(StringBuilder word) {
        int indexDot = word.length() - 1;
        if (word.charAt(indexDot) == '.') {
            word.deleteCharAt(indexDot);
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getListId() {
        return listId;
    }

    public ArrayList<Double> getListConstanst() {
        return listConstans;
    }

    public ArrayList<Token> getListTokens() {
        return listTokens;
    }

    public void showResultAnalyzer() {
        for (int i = 0; i < listId.size(); ++i) {
            System.out.println("ID" + i + "    " + listId.get(i));
        }
        System.out.println();

        for (int i = 0; i < listConstans.size(); ++i) {
            System.out.println("CL" + i + "    " + listConstans.get(i));
        }
        System.out.println();

        for (int i = 0; i < listTokens.size(); ++i) {
            Token token = listTokens.get(i);
            if (token.getTypeToken() == Token.TokensType.ID) {
                System.out.print(token.getTypeToken().toString() + token.getIndex() + " ");
                continue;
            }
            if (token.getTypeToken() == Token.TokensType.CI) {
                System.out.print(token.getTypeToken().toString() + token.getIndex() + " ");
                continue;
            }

            if (token.getTypeToken() == Token.TokensType.SRPT) {
                System.out.print(token.getTypeToken().toString() + " ");
                System.out.println();
                continue;
            }

            System.out.print(token.getTypeToken().toString() + " ");

        }
    }

    private void initMapKeyWord() {

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("Files/f"), "Cp1251"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        Scanner scan = new Scanner(line);

                        String tokenType = scan.next();
                        LinkedList<String> tokenValue = new LinkedList<>();

                        while (scan.hasNext()) {
                            String word = scan.next();
                            tokenValue.add(word);
                        }

                        System.out.printf("%s : %s\n", tokenType, tokenValue);

                        mapProcessSeqWord.put(tokenValue.get(0), new ProcessorSequenceWord(
                                tokenValue.toArray(new String[0]),
                                Token.TokensType.valueOf(tokenType)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

    /**
     * @param args
     * @throws TokenException
     * @throws IOException
     */
    public static void main(String[] args) throws TokenException, IOException {
        System.out.println("Working Directory = "
                + System.getProperty("user.dir"));
        FileInputStream fileStream = new FileInputStream("Files/source.chef");
        Analyzer analyzer = new Analyzer(fileStream);
        analyzer.showResultAnalyzer();
    }
}
