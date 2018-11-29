package vm;

import searchtree.*;
import searchtree.Exception;
import trail.TrailElement;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

public class TestableLogicVM extends LogicVM {
    public HashMap<String, Integer> inspectHeap() throws NoSuchFieldException, IllegalAccessException {
        Field field = LogicVM.class.getDeclaredField("heap");
        field.setAccessible(true);
        return (HashMap<String, Integer>) field.get(this);
    }
    public LinkedList<TrailElement> inspectTrail() throws NoSuchFieldException, IllegalAccessException {
        Field field = LogicVM.class.getDeclaredField("currentTrail");
        field.setAccessible(true);
        return (LinkedList<TrailElement>) field.get(this);
    }

    public void printDFS(ST tree, int depth) {
        if (tree instanceof Fail) {
            System.out.println(repeat(depth) + "- Fail");
        } else if (tree instanceof Exception) {
            System.out.println(repeat(depth) + "- Exception " + ((Exception)tree).exception);
        } else if (tree instanceof Value) {
            System.out.println(repeat(depth) + "- Value " + ((Value)tree).value);
        } else if (tree instanceof Choice) {

            System.out.println(repeat(depth) + "- Choice ");
            printDFS(((Choice) tree).st1, depth + 1);
            printDFS(((Choice) tree).st2, depth + 1);
        } else if (tree instanceof STProxy) {
            if (((STProxy)tree).isEvaluated()) printDFS(((STProxy)tree).eval(this), depth);
            else
                System.out.println(repeat(depth) + "- (not evaluated) ");
        } else {
            throw new IllegalStateException("Unknown tree node type.");
        }
    }

    private String repeat(int depth) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            result.append("    ");
        }
        return result.toString();
    }
}
