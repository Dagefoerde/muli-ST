package search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import search.TreeDFSIterator;
import searchtree.ST;
import searchtree.STProxy;
import vm.TestableLogicVM;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InfiniteDFSTest {
    TestableLogicVM vm;
    ST<Object> tree;

    @BeforeEach
    public void setUp() {
        vm = new TestableLogicVM();
        tree = new STProxy<>(0, null);
    }

    @Test
    public void FirstBranchOverflowsTest() {
        vm.setProgram(new examples.InfiniteComplicatedAssignmentCoin());
        assertThrows(StackOverflowError.class, () -> {
            TreeDFSIterator.stream(tree, vm).limit(1).collect(Collectors.toList());
        });
    }

}