import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import search.TreeBFSIterator;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InfiniteBFSTest {
    TestableLogicVM vm;
    ST<Object> tree;

    @BeforeEach
    public void setUp() {
        vm = new TestableLogicVM();
        tree = new STProxy<>(0, null);
    }

    @Test
    public void infiniteWithSideEffects2Test() throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.InfiniteComplicatedAssignmentCoin());
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(2).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        STDemo.printDFS(tree, 0, vm);

        assertEquals(2, leaves.size());

        assertIterableEquals(Arrays.asList(new Integer[]{1, 3}), leaves);

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

    @Test
    public void infiniteWithSideEffects6Test() throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.InfiniteComplicatedAssignmentCoin());
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(6).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        STDemo.printDFS(tree, 0, vm);

        assertEquals(6, leaves.size());

        assertIterableEquals(Arrays.asList(new Integer[]{1, 3, 1, 3, 1, 3}), leaves);

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = { 7, 10, 14, 15, 17, 99 })
    public void infiniteWithSideEffectsNTest(int n) throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.InfiniteComplicatedAssignmentCoin());
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(n).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        STDemo.printDFS(tree, 0, vm);

        assertEquals(n, leaves.size());

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

}