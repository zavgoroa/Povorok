/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SynatxAnalyzer;

import LexicalAnalyzer.Token;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.tree.DefaultMutableTreeNode;

public class AnalyzerSyntax {

    DefaultMutableTreeNode tree;
    Map<Symbol, Set<List<Symbol>>> grammaTable;

    public AnalyzerSyntax(Map<Symbol, Set<List<Symbol>>> grammaTable) throws IOException {
        tree = new DefaultMutableTreeNode();
        this.grammaTable = grammaTable;
    }

    public void start(ArrayList<Token> listTokens) {
        Stack<Symbol> stack = new Stack<>();

//        Stack<Token> stackTokens = new Stack<>();
//        for(int i = listTokens.size() - 1; i >= 0; --i) {
//            stackTokens.push(listTokens.get(i));
//        }
        Symbol key = grammaTable.keySet().iterator().next();
        stack.push(key);

        for (Token token : listTokens) {

            if (stack.peek().isTerminal()) {
                String typeToken = token.getTypeToken().toString();
                String rule = stack.peek().getValue();
                if (typeToken.equals(rule)) {
                    stack.pop();
                } else {
                    throw new IllegalArgumentException(typeToken + "!=" + rule);
                }
            } else {
                Symbol findSym = stack.peek();
                boolean flag = true;
                
                while (flag) {
                    Set<List<Symbol>> ruleForSym = grammaTable.get(findSym);
                    for (List<Symbol> list : ruleForSym) {
                        if (list.get(0).isTerminal()) {
                            if (list.get(0).getValue() == token.getTypeToken().toString()) {
                                
                                flag = false;
                                break;
                            }
                        } else {
                            findSym = list.get(0);
                            break;
                        }
                    }
                }
            }
        }

    }
}
