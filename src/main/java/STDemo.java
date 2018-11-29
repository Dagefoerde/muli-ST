import search.TreeDFSIterator;
import searchtree.*;
import vm.LogicVM;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class STDemo {
    public static void main(String[] args) {
        new STDemo();
    }

    public STDemo() {
        LogicVM vm = new LogicVM();
        //program = new SimpleCoin();
        //program = new ComplicatedCoin();
        //program = new InfiniteCoin();
        //program = new InfinitePrintingCoin();
        //program = new AssignmentCoin();
        //vm.setProgram(new ComplicatedAssignmentCoin()); // Expect: Traversiert: [0, 1, 3].
        //program = new InfiniteComplicatedAssignmentCoin();

        // Init execution.
        ST<Object> tree = new STProxy<>(0, null);

        //List<Object> leaves = strictDFS(tree);
        List<Object> leaves = TreeDFSIterator.stream(tree, vm).limit(4).collect(Collectors.toList());
        //List<Object> leaves = TreeBFSIterator.stream(tree, vm).limit(2).collect(Collectors.toList());
        System.out.print("Traversiert: ");
        Stream.of(leaves).forEach(System.out::println);
    }

}


