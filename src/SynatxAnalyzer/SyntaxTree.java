package SynatxAnalyzer;

import LexicalAnalyzer.Token;
import com.sun.javafx.fxml.expression.Expression;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

/**
 * Created by kodoo on 09.11.2015.
 */
public class SyntaxTree {

    public DefaultMutableTreeNode pointer;

    public SyntaxTree(Symbol root) {

        pointer = new DefaultMutableTreeNode(root);
    }

    public DefaultMutableTreeNode getRoot() {
        return (DefaultMutableTreeNode) pointer.getRoot();
    }

    void addChildren(List<Symbol> children) {

        //System.out.printf("add children to %s\n", pointer);

        //if empty symbol
        if (children.size() == 1 && children.get(0).getValue().equals(Token.TokensType.e.toString())) {
            pointer.add(new DefaultMutableTreeNode(new Symbol(Token.TokensType.e.toString())));
            //System.out.printf("E!\n");
            next();
            return;
        }

        for (Symbol child : children) {
            pointer.add(new DefaultMutableTreeNode(child));
            //System.out.printf("%s ", child);
        }

        pointer = pointer.getFirstLeaf();
        //System.out.printf("\npointer -> %s \n", pointer);
    }

    void next() {

        //System.out.printf("next from %s\n", pointer);
        while (pointer.getNextSibling() == null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) pointer.getParent();
            if (parent == null) {
                return;
            }

            pointer = parent;
            //System.out.printf("up to %s\n", pointer);
        }

        pointer = pointer.getNextSibling();
        //System.out.printf("right to %s\n", pointer);
    }

    public static String asString(DefaultMutableTreeNode node) {

        StringBuilder str = new StringBuilder();
        str.append("[");
        str.append(node.toString());

        if (node.getChildCount() > 1)
            str.append(" ");

        for (Enumeration<DefaultMutableTreeNode> children = node.children(); children.hasMoreElements();) {
            str.append(asString(children.nextElement()));

            str.append(" ");
        }

        str.append("]");

        return str.toString();
    }

    @Override
    public String toString() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) pointer.getRoot();
        return asString(root);
    }
}
