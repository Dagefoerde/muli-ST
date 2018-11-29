import org.junit.jupiter.api.Test;
import search.TreeBFSIterator;
import search.TreeDFSIterator;
import searchtree.ST;
import searchtree.STProxy;
import trail.TrailElement;
import vm.TestableLogicVM;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleDFSTest {
    @Test
    public void simpleCoinTest() throws NoSuchFieldException, IllegalAccessException {
        TestableLogicVM vm = new TestableLogicVM();
        vm.setProgram(new examples.SimpleCoin());
        ST<Object> tree = new STProxy<>(0, null);
        List<Object> leaves = TreeDFSIterator.stream(tree, vm).limit(2).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        STDemo.printDFS(tree, 0, vm);

        assertIterableEquals(leaves, Arrays.asList(new String[]{"coin || False", "coin && True"}));

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

    @Test
    public void assignmentCoinTest() throws NoSuchFieldException, IllegalAccessException {
        TestableLogicVM vm = new TestableLogicVM();
        vm.setProgram(new examples.AssignmentCoin());
        ST<Object> tree = new STProxy<>(0, null);
        List<Object> leaves = TreeDFSIterator.stream(tree, vm).limit(4).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        STDemo.printDFS(tree, 0, vm);

        // We intentionally requested up to 4 using `limit()` but the program only has 2 solutions.
        assertEquals(2, leaves.size());

        assertIterableEquals(leaves, Arrays.asList(new Integer[]{0, 1}));

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

}