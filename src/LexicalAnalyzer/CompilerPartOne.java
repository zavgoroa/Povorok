/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilerpartone;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author sashav2
 */
public class CompilerPartOne {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws compilerpartone.TokenException
     */
    public static void main(String[] args) throws FileNotFoundException, TokenException, IOException {
        FileInputStream fileStream = new FileInputStream("source.txt");
        Analizer analizer = new Analizer(fileStream);
        analizer.showResultAnalyzer();
    }

}

class TokenException extends Exception {

    public TokenException(String message) {
        super(message);
    }

}

class Token {

    private final TokensType type;
    private String[] keyWord;
    private int index;

    enum TokensType {

        ID,
        CI,
        SRPT,
        MARK,
        INGR,
        METHOD,
        M_G,
        M_KG,
        M_CHL,
        M_SHT,
        M_ML,
        M_L,
        M_STL,
        STDIN,
        STDOUT,
        STDOUTSTACK,
        PUSH,
        POP,
        ADD,
        SUB,
        MUL,
        DIV,
        LCAST,
        STACKCAST,
        CLR,
        WHILE,
        UNTIL,
        BREAK
    }

    public Token(TokensType type, String[] keyWord) {
        this.type = type;
        this.keyWord = keyWord.clone();
        this.index = -1;
    }

    public Token(TokensType type, int index) {
        this.type = type;
        this.index = index;
        this.keyWord = new String[]{};
    }

    public TokensType getTypeToken() {
        return type;
    }

    public int getIndex() {
        return this.index;
    }

    public String[] getKeyWordForTokens() {
        return keyWord;
    }
}

class ProcessorSequenceWord {

    private final String[] sequenceWord;
    private final Token.TokensType tokenType;

    public ProcessorSequenceWord(String[] sequenceWord, Token.TokensType tokenType) {
        this.sequenceWord = sequenceWord.clone();
        this.tokenType = tokenType;
    }

    public int sizeSequenceWord() {
        return sequenceWord.length;
    }

    public Token processSequence(String[] words, Integer startIndex) throws TokenException {
        int index;
        for (index = 0; index < sequenceWord.length && index < words.length; ++index) {
            if (!sequenceWord[index].equals(words[startIndex])) {
                throw new TokenException("Incorrect languages contruction");
            }
            startIndex++;
        }
        if (index != sequenceWord.length) {
            throw new TokenException("Incorrect languages contruction");
        }
        return new Token(this.tokenType, this.sequenceWord);
    }
}

class Analizer {

    Map<String, ProcessorSequenceWord> mapProcessSeqWord;
    ArrayList<Token> listTokens;
    ArrayList<Double> listConstans;
    ArrayList<String> listId;

    public Analizer(FileInputStream fileStream) throws TokenException, IOException {
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
        mapProcessSeqWord.put("Нужно", new ProcessorSequenceWord(
                new String[]{"Нужно", "купить"},
                Token.TokensType.INGR));
        mapProcessSeqWord.put("Способ", new ProcessorSequenceWord(
                new String[]{"Способ", "приготовления"},
                Token.TokensType.METHOD));
        mapProcessSeqWord.put("г", new ProcessorSequenceWord(
                new String[]{"г"},
                Token.TokensType.M_G));
        mapProcessSeqWord.put("кг", new ProcessorSequenceWord(
                new String[]{"кг"},
                Token.TokensType.M_KG));
        mapProcessSeqWord.put("ч.л", new ProcessorSequenceWord(
                new String[]{"ч.л"},
                Token.TokensType.M_CHL));
        mapProcessSeqWord.put("шт", new ProcessorSequenceWord(
                new String[]{"шт"},
                Token.TokensType.M_SHT));
        mapProcessSeqWord.put("мл", new ProcessorSequenceWord(
                new String[]{"мл"},
                Token.TokensType.M_ML));
        mapProcessSeqWord.put("л", new ProcessorSequenceWord(
                new String[]{"л"},
                Token.TokensType.M_L));
        mapProcessSeqWord.put("ст.л", new ProcessorSequenceWord(
                new String[]{"ст.л"},
                Token.TokensType.M_STL));
        mapProcessSeqWord.put("Возьмите", new ProcessorSequenceWord(
                new String[]{"Возьмите", "из", "холодильника"},
                Token.TokensType.STDIN));
        mapProcessSeqWord.put("Положите", new ProcessorSequenceWord(
                new String[]{"Положите", "в", "холодильник"},
                Token.TokensType.STDOUT));
        mapProcessSeqWord.put("Отправте", new ProcessorSequenceWord(
                new String[]{"Отправте", "блюдо", "в", "холодильник", "часов", "на"},
                Token.TokensType.STDOUTSTACK));
        mapProcessSeqWord.put("Добавте", new ProcessorSequenceWord(
                new String[]{"Добавте"},
                Token.TokensType.PUSH));
        mapProcessSeqWord.put("Положите", new ProcessorSequenceWord(
                new String[]{"Положите"},
                Token.TokensType.POP));
        mapProcessSeqWord.put("Почистите", new ProcessorSequenceWord(
                new String[]{"Почистите"},
                Token.TokensType.ADD));
        mapProcessSeqWord.put("Натрите", new ProcessorSequenceWord(
                new String[]{"Натрите", "на", "тёрке"},
                Token.TokensType.SUB));
        mapProcessSeqWord.put("Замешайте", new ProcessorSequenceWord(
                new String[]{"Замешайте"},
                Token.TokensType.MUL));
        mapProcessSeqWord.put("Нарезать", new ProcessorSequenceWord(
                new String[]{"Нарезать"},
                Token.TokensType.DIV));
        mapProcessSeqWord.put("Вымочить", new ProcessorSequenceWord(
                new String[]{"Вымочить"},
                Token.TokensType.LCAST));
        mapProcessSeqWord.put("Добавить", new ProcessorSequenceWord(
                new String[]{"Добавить", "воды"},
                Token.TokensType.STACKCAST));
        mapProcessSeqWord.put("Подготовте", new ProcessorSequenceWord(
                new String[]{"Подготовте", "чистую", "посуду"},
                Token.TokensType.CLR));
        mapProcessSeqWord.put("Начинайте", new ProcessorSequenceWord(
                new String[]{"Начинайте"},
                Token.TokensType.WHILE));
        mapProcessSeqWord.put("до", new ProcessorSequenceWord(
                new String[]{"до"},
                Token.TokensType.UNTIL));
        mapProcessSeqWord.put("Отложите", new ProcessorSequenceWord(
                new String[]{"Отложите", "всё", "в", "сторону"},
                Token.TokensType.BREAK));
    }
}
