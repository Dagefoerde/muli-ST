package search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import search.TreeBFSIterator;
import searchtree.Choice;
import searchtree.ST;
import searchtree.STProxy;
import searchtree.Value;
import trail.TrailElement;
import vm.TestableLogicVM;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBFSTest {
    TestableLogicVM vm;
    STProxy<Object> tree;

    @BeforeEach
    public void setUp() {
        vm = new TestableLogicVM();
        tree = new STProxy<>(0, null);
    }

    @Test
    public void simpleCoinTest() throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.SimpleCoin());
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(2).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        vm.printDFS(tree, 0);

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
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(4).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        vm.printDFS(tree, 0);

        // We intentionally requested up to 4 using `limit()` but the program only has 2 solutions.
        assertEquals(2, leaves.size());

        assertIterableEquals(leaves, Arrays.asList(new Integer[]{0, 1}));

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

    @Test
    public void complicatedAssignmentCoinTest() throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.ComplicatedAssignmentCoin());
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(4).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        vm.printDFS(tree, 0);

        // We intentionally requested up to 4 using `limit()` but the program only has 3 solutions.
        assertEquals(3, leaves.size());

        assertIterableEquals(leaves, Arrays.asList(new Integer[]{0, 1, 3}));

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

    @Test
    public void complicatedAssignmentCoinWithHardLimitTest() throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.ComplicatedAssignmentCoin());
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(2).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        vm.printDFS(tree, 0);

        assertEquals(2, leaves.size());

        assertIterableEquals(leaves, Arrays.asList(new Integer[]{0, 1}));

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

    @Test
    public void complicatedCoinWithTest() throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.ComplicatedCoin());
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(8).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        vm.printDFS(tree, 0);

        // Intentional limit 8 vs. expect 4.
        assertEquals(4, leaves.size());

        assertIterableEquals(leaves, Arrays.asList("coin1 || False", "coin1 || coin2",
                "coin1 && coin2", "coin1 && True"));

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

    @Test
    public void infiniteComplicatedAssignmentCoinTest() throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.InfiniteComplicatedAssignmentCoin());
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(2).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        vm.printDFS(tree, 0);

        assertEquals(2, leaves.size());

        assertTrue(((Choice)tree.eval(vm)).st1.isEvaluated());
        assertTrue(((Choice)tree.eval(vm)).st2.isEvaluated());
        assertFalse(((Choice)((Choice)((Choice)tree.eval(vm)).st1.eval(vm)).st1.eval(vm)).st1.isEvaluated());
        assertTrue((((Choice)((Choice)tree.eval(vm)).st2.eval(vm)).st1.eval(vm)) instanceof Value);
        assertTrue((((Choice)((Choice)tree.eval(vm)).st2.eval(vm)).st2.eval(vm)) instanceof Value);
        assertEquals(1, ((Value)(((Choice)((Choice)tree.eval(vm)).st2.eval(vm)).st1.eval(vm))).value);
        assertEquals(3, ((Value)(((Choice)((Choice)tree.eval(vm)).st2.eval(vm)).st2.eval(vm))).value);

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());
    }

    @Test
    public void complicatedCoinWithHardLimitTest() throws NoSuchFieldException, IllegalAccessException {
        vm.setProgram(new examples.ComplicatedCoin());
        List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(1).collect(Collectors.toList());

        Stream.of(leaves).forEach(System.out::println);
        vm.printDFS(tree, 0);

        assertIterableEquals(leaves, Arrays.asList("coin1 || False"));

        HashMap<String, Integer> heap = vm.inspectHeap();
        assertTrue(heap.isEmpty());

        LinkedList<TrailElement> trail = vm.inspectTrail();
        assertTrue(trail.isEmpty());

        // TODO Test that branch 2 is "Choice" with two children, each "(not evaluated)".
    }

}