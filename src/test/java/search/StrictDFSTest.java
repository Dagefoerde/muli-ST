package search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchtree.ST;
import searchtree.STProxy;
import trail.TrailElement;
import vm.TestableLogicVM;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StrictDFSTest {
    TestableLogicVM vm;
    ST<Object> tree;

    @BeforeEach
    public void setUp() {
        vm = new TestableLogicVM();
        tree = new STProxy<>(0, null);
    }

    @Test
    public void simpleCoinTest() throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.SimpleCoin());
        List<Object> leaves = vm.strictDFS(tree);

        Stream.of(leaves).forEach(System.out::println);
        vm.printDFS(tree, 0);

        assertIterableEquals(leaves, Arrays.asList("coin || False", "coin && True"));

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

    @Test
    public void StackOverflowsTest() {
        vm.setProgram(new examples.InfiniteComplicatedAssignmentCoin());
        assertThrows(StackOverflowError.class, () -> {
            vm.strictDFS(tree);
        });
    }
}