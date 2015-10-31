package LexicalAnalyzer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
                    addConstants(words[i]);
                    saveReadWord = addToken(true, saveReadWord);
                    continue;
                }

                ProcessorSequenceWord keyWordProcess = mapProcessSeqWord.get(words[i]);
                if (keyWordProcess == null) {
                    saveReadWord += words[i] + " ";
                } else {
                    Token token = keyWordProcess.processSequence(words, i);
                    i += keyWordProcess.sizeSequenceWord() - 1;
                    listTokens.add(token);
                }
            }
            saveReadWord = addToken(true, saveReadWord);
            listTokens.add(new Token(Token.TokensType.SRPT, -1));
        }
    }

    private String addToken(boolean state, String id) {
        if (state && !id.isEmpty()) {
            listId.add(id);
            listTokens.add(new Token(Token.TokensType.ID, listId.size() - 1));
            //listTokens.add(new Token(Token.TokensType.SRPT, -1));
            id = "";
        }
        return id;
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
        mapProcessSeqWord.put("�����", new ProcessorSequenceWord(
                new String[]{"�����", "������"},
                Token.TokensType.INGR));
        mapProcessSeqWord.put("������", new ProcessorSequenceWord(
                new String[]{"������", "�������������"},
                Token.TokensType.METHOD));
        mapProcessSeqWord.put("�", new ProcessorSequenceWord(
                new String[]{"�"},
                Token.TokensType.M_G));
        mapProcessSeqWord.put("��", new ProcessorSequenceWord(
                new String[]{"��"},
                Token.TokensType.M_KG));
        mapProcessSeqWord.put("�.�", new ProcessorSequenceWord(
                new String[]{"�.�"},
                Token.TokensType.M_CHL));
        mapProcessSeqWord.put("��", new ProcessorSequenceWord(
                new String[]{"��"},
                Token.TokensType.M_SHT));
        mapProcessSeqWord.put("��", new ProcessorSequenceWord(
                new String[]{"��"},
                Token.TokensType.M_ML));
        mapProcessSeqWord.put("�", new ProcessorSequenceWord(
                new String[]{"�"},
                Token.TokensType.M_L));
        mapProcessSeqWord.put("��.�", new ProcessorSequenceWord(
                new String[]{"��.�"},
                Token.TokensType.M_STL));
        mapProcessSeqWord.put("��������", new ProcessorSequenceWord(
                new String[]{"��������", "��", "������������"},
                Token.TokensType.STDIN));
        mapProcessSeqWord.put("��������", new ProcessorSequenceWord(
                new String[]{"��������", "�", "�����������"},
                Token.TokensType.STDOUT));
        mapProcessSeqWord.put("��������", new ProcessorSequenceWord(
                new String[]{"��������", "�����", "�", "�����������", "�����", "��"},
                Token.TokensType.STDOUTSTACK));
        mapProcessSeqWord.put("�������", new ProcessorSequenceWord(
                new String[]{"�������"},
                Token.TokensType.PUSH));
        mapProcessSeqWord.put("��������", new ProcessorSequenceWord(
                new String[]{"��������"},
                Token.TokensType.POP));
        mapProcessSeqWord.put("���������", new ProcessorSequenceWord(
                new String[]{"���������"},
                Token.TokensType.ADD));
        mapProcessSeqWord.put("�������", new ProcessorSequenceWord(
                new String[]{"�������", "��", "����"},
                Token.TokensType.SUB));
        mapProcessSeqWord.put("���������", new ProcessorSequenceWord(
                new String[]{"���������"},
                Token.TokensType.MUL));
        mapProcessSeqWord.put("��������", new ProcessorSequenceWord(
                new String[]{"��������"},
                Token.TokensType.DIV));
        mapProcessSeqWord.put("��������", new ProcessorSequenceWord(
                new String[]{"��������"},
                Token.TokensType.LCAST));
        mapProcessSeqWord.put("��������", new ProcessorSequenceWord(
                new String[]{"��������", "����"},
                Token.TokensType.STACKCAST));
        mapProcessSeqWord.put("����������", new ProcessorSequenceWord(
                new String[]{"����������", "������", "������"},
                Token.TokensType.CLR));
        mapProcessSeqWord.put("���������", new ProcessorSequenceWord(
                new String[]{"���������"},
                Token.TokensType.WHILE));
        mapProcessSeqWord.put("��", new ProcessorSequenceWord(
                new String[]{"��"},
                Token.TokensType.UNTIL));
        mapProcessSeqWord.put("��������", new ProcessorSequenceWord(
                new String[]{"��������", "��", "�", "�������"},
                Token.TokensType.BREAK));
    }

    /**
     * @param args
     * @throws TokenException
     * @throws IOException
     */
    public static void main(String[] args) throws TokenException, IOException {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        FileInputStream fileStream = new FileInputStream("Files/source.chef");
        Analyzer analyzer = new Analyzer(fileStream);
        analyzer.showResultAnalyzer();
    }
}
