package LexicalAnalyzer;

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

