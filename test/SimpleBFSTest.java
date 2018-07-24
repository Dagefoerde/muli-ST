import org.junit.jupiter.api.Test;
import search.TreeBFSIterator;
import searchtree.ST;
import searchtree.STProxy;
import trail.TrailElement;
import vm.TestableLogicVM;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBFSTest {
    @Test
    public void simpleCoinTest() throws NoSuchFieldException, IllegalAccessException {
        TestableLogicVM vm = new TestableLogicVM();
        vm.setProgram(new examples.SimpleCoin());
        ST<Object> tree = new STProxy<>(0, null);
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(2).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        STDemo.printDFS(tree, 0, vm);

        assertIterableEquals(leaves, Arrays.asList(new String[]{"coin || False", "coin && True"}));

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

}