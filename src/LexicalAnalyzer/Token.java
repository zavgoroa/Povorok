package LexicalAnalyzer;

/**
 * Created by kodoo on 09.10.2015.
 */
public class Token {

    enum Type
    {
        ID, CI, SRPT, MARK, INGR, METHOD, M_G, M_KG, M_CHL, M_SHT, M_ML, M_L,
        M_STL, STDIN, STDOUT, STDOUTSTACK, PUSH, POP, ADD, SUB, MUL, DIV, LCAST,
        STACKCAST, CLR, WHILE, UNTIL, BREAK;
    }

    Token(Type type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Индекс нужен для ID, CI и MARK
     */
    private int index = 0;
    private Type type;
}
