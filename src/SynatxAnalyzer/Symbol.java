package SynatxAnalyzer;

import LexicalAnalyzer.Token;

/**
 * Created by kodoo on 01.11.2015.
 */
public class Symbol {

    private String type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;
    private boolean isTerminal;

    public Symbol(String type) {
        this.type = type;
        this.isTerminal = Token.isTokenType(type);
        this.value = "_";
    }

    public String getType() {
        return type;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    @Override
    public String toString() {
        return type + (isTerminal ? "[" + value + "]" + "(t)" : "(n)");
    }

    public String toStringClear() {
        return type + (isTerminal ? "(t)" : "(n)");
    }

    @Override
    public boolean equals(Object obj) {
        return type.equals(((Symbol)obj).getType());
    }
}
