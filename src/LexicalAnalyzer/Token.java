package LexicalAnalyzer;

/**
 * Created by kodoo on 09.10.2015.
 */

public class Token {

    private final TokensType type;
    private String[] keyWord;
    private int index;

    public enum TokensType {
        ID, CI, SRPT, MARK, INGR, METHOD, M_G, M_KG, M_CHL, M_SHT, M_ML, M_L,
        M_STL, STDIN, STDOUT, STDOUTSTACK, PUSH, POP, ADD, SUB, MUL, DIV, LCAST,
        STACKCAST, CLR, WHILE, UNTIL, BREAK, IF, ELSE, NEQ, EQ, GR, LES, ENDIF,
        AND, SET, GETBYINDEX;
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

    static public boolean isTokenType(String str) {
        try {
            TokensType.valueOf(str);
        }
        catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }
}

