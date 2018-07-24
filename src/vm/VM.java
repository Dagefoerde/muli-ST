package vm;

import examples.Program;
import examples.SimpleCoin;
import searchtree.Choice;
import searchtree.ST;
import trail.TrailElement;

import java.util.LinkedList;

public interface VM {
    void setPC(int pc);

    <A> ST<A> execute();

    void setVar(String key, int value);

    void println(String s);

    int getVar(String key);

    void applyState(LinkedList<TrailElement> trail);

    void setCurrentChoice(Choice choice);

    void revertState(LinkedList<TrailElement> trail);

    LinkedList<TrailElement> getCurrentTrail();

    <T> Choice<T> getCurrentChoice();

    void setProgram(Program program);
}
