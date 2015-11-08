package SynatxAnalyzer;

import LexicalAnalyzer.Token;

/**
 * Created by kodoo on 01.11.2015.
 */
public class Symbol {

    private String  value;
    private boolean isTerminal;

    public Symbol(String value) {
        this.value = value;
        this.isTerminal = Token.isTokenType(value);
    }

    public String getValue() {
        return value;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    @Override
    public String toString() {
        return value + (isTerminal ? "(t)" : "(n)");
    }

    @Override
    public boolean equals(Object obj) {
        return value.equals(((Symbol)obj).getValue());
    }
}
