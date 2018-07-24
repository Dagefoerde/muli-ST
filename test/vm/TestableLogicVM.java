package vm;

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
}
