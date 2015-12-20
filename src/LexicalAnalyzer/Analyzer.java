package LexicalAnalyzer;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by kodoo on 09.10.2015.
 */
public class Analyzer {

    Map<String, ProcessorSequenceWord> mapProcessSeqWord;
    ArrayList<Token> listTokens;
    ArrayList<Integer> listConstans;
    ArrayList<String> listId;

    public Analyzer() throws TokenException, IOException {
        mapProcessSeqWord = new HashMap<>();
        listTokens = new ArrayList<>();
        listId = new ArrayList<>();
        listConstans = new ArrayList<>();
        initMapKeyWord();
    }

    public boolean parcingText(FileInputStream input) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        br.skip(1);

        StringBuilder wordReaded = new StringBuilder();
        int countLine = 0;
        int posInLine = 0;
        int numWordInKeyPhrase = 0;
        boolean isKeyPhrase = false;
        boolean statusLexicalAnalize = true;
        ProcessorSequenceWord keyWordProcess = null;

        int ch = 0;
        while ((ch = br.read()) != -1) {
            char symbol = (char) ch;

            if (symbol == '\r') {
                posInLine++;
                continue;
            }

            if (symbol != '.' && symbol != ' ' && symbol != '\n' && symbol != '\t') {
                wordReaded.append(symbol);
                posInLine++;
            } else {
                String word = wordReaded.toString().trim().toLowerCase();
                if (!word.isEmpty())
                    System.out.println(word);

                if (!word.isEmpty()) {

                    if (isNumeric(word)) {
                        addConstants(word);
                        wordReaded.delete(0, wordReaded.length());
                        continue;
                    }


                    if (!isTrueIdentificator(word)) {
                        System.out.println("Error in line " + countLine +
                                " , position " + (posInLine - word.length())
                                + ", word= \"" + word + "\"");
                        wordReaded.delete(0, wordReaded.length());
                        statusLexicalAnalize = false;
                        isKeyPhrase = false;
                        //continue;

                    } else {

                        if (!isKeyPhrase) {
                            keyWordProcess = mapProcessSeqWord.get(word);
                            if (keyWordProcess == null) {
                                checkId(word);
                            } else {
                                numWordInKeyPhrase = keyWordProcess.sizeSequenceWord() - 1;

                                if (numWordInKeyPhrase == 0) {
                                    addToken(keyWordProcess.getTokenType(), -1);
                                    isKeyPhrase = false;
                                } else {
                                    isKeyPhrase = true;
                                }
                            }
                        } else {
                            if (keyWordProcess.checkWord(word, keyWordProcess.sizeSequenceWord() - numWordInKeyPhrase)) {
                                numWordInKeyPhrase--;
                                if (numWordInKeyPhrase <= 0) {
                                    isKeyPhrase = false;
                                    addToken(keyWordProcess.getTokenType(), -1);
                                }
                            } else {
                                System.out.println("Token error in line " +
                                        countLine + " , position "
                                        + (posInLine - word.length()) + ", word="
                                        + word + ", expected phrase="
                                        + keyWordProcess.getWordSequence());
                                statusLexicalAnalize = false;
                                isKeyPhrase = false;
                            }
                        }
                    }
                } else {
                    if (isKeyPhrase && symbol != '\t' && symbol != '\n' && symbol != ' ') {
                        System.out.println("Token error in line " +
                                countLine + " , position "
                                + (posInLine - word.length()) + ", word ="
                                + word + ", expected phrase="
                                + keyWordProcess.getWordSequence());
                        statusLexicalAnalize = false;
                        isKeyPhrase = false;
                    }
                }

                wordReaded.delete(0, wordReaded.length());
                posInLine++;

                if (symbol == '.') {
                    addToken(Token.TokensType.SRPT, -1);
                }

                if (symbol == '\n') {
                    countLine++;
                    posInLine = 0;
                }
            }
        }
        return statusLexicalAnalize;
    }

    private void checkId(String id) {
        if (!id.isEmpty()) {
            int index = listId.indexOf(id);
            if (index < 0) {
                listId.add(id);
                addToken(Token.TokensType.ID, listId.size() - 1);
            } else {
                addToken(Token.TokensType.ID, index);
            }
        }
    }

    private boolean isTrueIdentificator(String word) {
        return word.matches("^[а-я][а-я0-9]*");
    }

    private void addToken(Token.TokensType type, int index) {
        Token token = new Token(type);
        token.setIndex(index);
        listTokens.add(token);
    }

    private void addConstants(String word) {
        Integer value = Integer.parseInt(word);
        listConstans.add(value);
        addToken(Token.TokensType.CI, listConstans.size() - 1);
    }

    private boolean isNumeric(String word) {
        return word.matches("-?\\d+(\\.\\d+)?");
    }

    public ArrayList<String> getListId() {
        return (ArrayList<String>) listId.clone();
    }

    public ArrayList<Double> getListConstanst() {
        return (ArrayList<Double>) listConstans.clone();
    }

    public ArrayList<Token> getListTokens() {
        return (ArrayList<Token>) listTokens.clone();
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
            if (token.getTokenType() == Token.TokensType.ID) {
                System.out.print(token.getTokenType().toString() + token.getIndex() + " ");
                continue;
            }
            if (token.getTokenType() == Token.TokensType.CI) {
                System.out.print(token.getTokenType().toString() + token.getIndex() + " ");
                continue;
            }

            if (token.getTokenType() == Token.TokensType.SRPT) {
                System.out.print(token.getTokenType().toString() + " ");
                System.out.println();
                continue;
            }

            System.out.print(token.getTokenType().toString() + " ");

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
                    tokenValue.add(word.toLowerCase());
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

    public static void main(String[] args) throws TokenException, IOException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        FileInputStream fileStream = new FileInputStream("Files/source.chef");
        Analyzer analyzer = new Analyzer();
        if (!analyzer.parcingText(fileStream)) return;
        System.out.println();
        analyzer.showResultAnalyzer();

//        Stack<Token> st = new Stack<>();
//        st.addAll(analyzer.getListTokens());
//        Collections.reverse(st);
//
//        while (!st.isEmpty()) {
//            System.out.println(st.pop().getTokenType());
//        }
    }
}
