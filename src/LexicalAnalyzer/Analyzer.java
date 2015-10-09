package LexicalAnalyzer;

import java.util.LinkedList;

/**
 * Created by kodoo on 09.10.2015.
 */
public class Analyzer {

    LinkedList<Token>     tokens;

    // Положения объекта в таблице соответствует
    // Так нулевой элемент это ID0, 1ый элемент списка - ID1 и т.д.
    LinkedList<String> idTable;
    // То же самое с Int константами CI
    LinkedList<Integer>   constantTable;
    // То же самое с маркерами циклов MARK
    LinkedList<Integer>   markTable;

    public LinkedList<Token> getTokens() {
        return tokens;
    }

    public LinkedList<String> getIdTable() {
        return idTable;
    }

    public LinkedList<Integer> getConstantTable() {
        return constantTable;
    }

    public LinkedList<Integer> getMarkTable() {
        return markTable;
    }

    /**
     * Вся работа проходит во время инстанцирования анализатора
     * @param source - исходный код
     */
    public Analyzer(String source) {

        tokens          = new LinkedList<>();
        idTable         = new LinkedList<>();
        constantTable   = new LinkedList<>();
        markTable       = new LinkedList<>();

        //Пример создания токен
        Token t = new Token(Token.Type.ID);
        //добавили в список токенов
        tokens.push(t);
        //т.к. это был ID, то добавили его значение в таблицу ID
        idTable.push("Макарохи");
        // связать индекс токена с положением его значения в таблице
        t.setIndex(idTable.indexOf(t));

        //TODO: работа анализатора, формирования таблиц и коллекции токенов
    }

}
