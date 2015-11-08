package LexicalAnalyzer;

import java.util.Scanner;

/**
 * Created by kodoo on 31.10.2015.
 */
class ProcessorSequenceWord {

    private final String[] sequenceWord;
    private final Token.TokensType tokenType;

    public ProcessorSequenceWord(String[] sequenceWord, Token.TokensType tokenType) {
        this.sequenceWord = sequenceWord.clone();
        this.tokenType = tokenType;
    }

    public String getWordSequence() {
        String str = "";
        for (String word: sequenceWord) {
            str += word + " ";
        }
        return str;
    }
    public int sizeSequenceWord() {
        return sequenceWord.length;
    }
    
    public Token.TokensType getTokenType() {
        return this.tokenType;
    }

    public boolean checkWord(String word, int index) {
        return sequenceWord[index].equals(word);
    }
}

