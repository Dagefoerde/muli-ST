import examples.ComplicatedAssignmentCoin;
import examples.Program;
import search.TreeDFSIterator;
import searchtree.*;
import searchtree.Exception;
import trail.TrailElement;
import trail.VariableChanged;
import vm.VM;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class STDemo implements VM {
    private boolean restoringMode = false;
    private Choice currentChoice;

    public static void main(String[] args) {
        new STDemo();
    }

    private int pc;

    private Program program;
    private HashMap<String, Integer> heap;
    private LinkedList<TrailElement> currentTrail;

    public STDemo() {
        //program = new SimpleCoin();
        //program = new ComplicatedCoin();
        //program = new InfiniteCoin();
        //program = new InfinitePrintingCoin();
        //program = new AssignmentCoin();
        this.setProgram(new ComplicatedAssignmentCoin());
        //program = new InfiniteComplicatedAssignmentCoin();

        // Init execution.
        heap = new HashMap<>();
        currentTrail = new LinkedList<>();
        ST<Object> tree = new STProxy<>(0, null);

        //List<Object> leaves = strictDFS(tree);
        List<Object> leaves = TreeDFSIterator.stream(tree, this).limit(2).collect(Collectors.toList());
        //List<Object> leaves = TreeBFSIterator.stream(tree, this).limit(2).collect(Collectors.toList());
        System.out.print("Traversiert: ");
        Stream.of(leaves).forEach(System.out::println);
        //printDFS(tree, 0, this);

        System.out.println("Heap after (expect empty):");
        heap.forEach((k,v) -> System.out.println(String.format("%s:%s",k, v)));

    }

    public List strictDFS(ST tree) {
        if (tree instanceof Fail) {
            this.revertState(this.extractCurrentTrail());
            return Collections.EMPTY_LIST;
        } else if (tree instanceof Exception) {
            ArrayList l = new ArrayList();
            l.add(((Exception)tree).exception);
            this.revertState(this.extractCurrentTrail());
            return l;
        } else if (tree instanceof Value) {
            ArrayList l = new ArrayList();
            l.add(((Value)tree).value);
            this.revertState(this.extractCurrentTrail());
            return l;
        } else if (tree instanceof Choice) {
            // Apply state from `previousState`:
            // Not needed, because we got here via strict DFS and sibling branches remove their effects themselves.
            // this.applyState(previousState); (might be needed for lazy strategies.)
            Choice c = (Choice) tree;
            List list = strictDFS(c.st1);
            // Intermediate effects were removed by strictDFS.
            list.addAll(strictDFS(c.st2));
            this.revertState(c.trail);
            return list;
        } else if (tree instanceof STProxy) {
            STProxy uneval = (STProxy) tree;
            return strictDFS(uneval.eval(this));
        } else {
            throw new IllegalStateException("Unknown tree node type.");
        }
    }

    @Override
    public void setPC(int pc) {
        this.pc = pc;
    }

    public int getPC() {
        return this.pc;
    }

    @Override
    public ST execute() {
        return program.execute(this, this.getPC());
    }

    public void println(String s) {
        System.out.println(s);
    }

    public void setVar(String key, int val) {
        Optional<Integer> formerValue = Optional.ofNullable(heap.get(key));
        VariableChanged variableChanged = new VariableChanged(key, formerValue, val);
        heap.put(key, val);
        if (!this.restoringMode) {
            currentTrail.push(variableChanged);
        }
    }

    public int getVar(String key) {
        return heap.get(key);
    }

    public LinkedList<TrailElement> extractCurrentTrail() {
        LinkedList<TrailElement> trail = this.currentTrail;
        this.currentTrail = new LinkedList<>();
        return trail;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    public void applyState(LinkedList<TrailElement> previousState) {
        this.restoringMode = true;
        for (TrailElement e : previousState) {
            if (e instanceof VariableChanged) {
                VariableChanged vc = (VariableChanged) e;
                this.setVar(vc.variable, vc.newValue);
            }
        }
        this.restoringMode = false;
    }

    public void revertState(LinkedList<TrailElement> previousState) {
        this.restoringMode = true;
        for (Iterator<TrailElement> it = previousState.descendingIterator(); it.hasNext(); ) {
            TrailElement e = it.next();
            if (e instanceof VariableChanged) {
                VariableChanged vc = (VariableChanged) e;
                if (vc.formerValue.isPresent()) {
                    this.setVar(vc.variable, vc.formerValue.get());
                } else {
                    this.removeVar(vc.variable);
                }
            }
        }
        this.restoringMode = false;
    }

    private void removeVar(String variable) {
        if (!this.restoringMode) {
            throw new IllegalStateException("Precondition violated: Must not be used outside restoring mode.");
        }
        heap.remove(variable);
    }

    public Choice getCurrentChoice() {
        return currentChoice;
    }

    public void setCurrentChoice(Choice currentChoice) {
        this.currentChoice = currentChoice;
    }
}


