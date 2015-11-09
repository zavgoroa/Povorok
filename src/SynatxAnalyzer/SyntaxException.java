package SynatxAnalyzer;

import LexicalAnalyzer.Token;

/**
 * Created by kodoo on 10.11.2015.
 */
public class SyntaxException extends Exception {

    public Symbol a;
    public Token t;

    SyntaxException(Symbol a, Token t) {
        this.a = a;
        this.t = t;
    }

}
