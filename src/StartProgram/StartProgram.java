/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StartProgram;

import LexicalAnalyzer.*;
import SynatxAnalyzer.*;
import java.io.FileInputStream;
import java.io.IOException;

public class StartProgram {

    public static void main(String[] args) throws TokenException, IOException {
        System.out.println("Working Directory = "+ System.getProperty("user.dir"));
        FileInputStream fileStream = new FileInputStream("Files/source.chef");
        Analyzer analyzer = new Analyzer(fileStream);
        
        System.out.println();
        
        analyzer.showResultAnalyzer();

        System.out.println();

        Grammar gr = new Grammar();
        gr.loadFromFile("Files/rules.txt", "Cp1251");
        System.out.print(gr);

        System.out.println();

        AnalyzerSyntax analizeSyntax = new AnalyzerSyntax(gr.getGrammar());
        analizeSyntax.start(analyzer.getListTokens());
    }
}
