package LexicalAnalyzer;

/**
 * Created by kodoo on 09.10.2015.
 */

class Token {

    private final TokensType type;
    private String[] keyWord;
    private int index;

    enum TokensType {
        ID, CI, SRPT, MARK, INGR, METHOD, M_G, M_KG, M_CHL, M_SHT, M_ML, M_L,
        M_STL, STDIN, STDOUT, STDOUTSTACK, PUSH, POP, ADD, SUB, MUL, DIV, LCAST,
        STACKCAST, CLR, WHILE, UNTIL, BREAK, IF, ELSE, NEQ, EQ, GR, LES, ENDIF;
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

