import searchtree.*;
import searchtree.Exception;
import trail.TrailElement;
import trail.VariableChanged;
import vm.VM;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class STDemo implements VM {
    private boolean restoringMode = false;
    private Choice currentChoice;

    public static void main(String[] args) {
        new STDemo();
    }

    private int pc;

    private Program program;
    private HashMap<String, Integer> heap;
    private Stack<TrailElement> currentTrail;

    public STDemo() {
        //program = new SimpleCoin();
        //program = new ComplicatedCoin();
        //program = new InfiniteCoin();
        //program = new InfinitePrintingCoin();
        //program = new AssignmentCoin();
        program = new ComplicatedAssignmentCoin();

        // Init execution.
        heap = new HashMap<>();
        currentTrail = new Stack<>();
        ST<Object> tree = new STProxy<>(0, null);

        //List<Object> leaves = strictDFS(tree);
        List<Object> leaves = lazyBFS(tree).limit(6).collect(Collectors.toList());
        System.out.print("Traversiert: ");
        Stream.of(leaves).forEach(System.out::println);
        printDFS(tree, 0);

        System.out.println("Heap after (expect empty):");
        heap.forEach((k,v) -> System.out.println(String.format("%s:%s",k, v)));

    }

    public List strictDFS(ST tree) {
        if (tree instanceof Fail) {
            this.revertState(this.getCurrentTrail());
            return Collections.EMPTY_LIST;
        } else if (tree instanceof Exception) {
            ArrayList l = new ArrayList();
            l.add(((Exception)tree).exception);
            this.revertState(this.getCurrentTrail());
            return l;
        } else if (tree instanceof Value) {
            ArrayList l = new ArrayList();
            l.add(((Value)tree).value);
            this.revertState(this.getCurrentTrail());
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

    public Stream<Object> lazyDFS(ST tree) {
        return StreamSupport.stream(new TreeDFSIterator<>(tree, this), false);
    }

    public Stream<Object> lazyBFS(ST tree) {
        return StreamSupport.stream(new TreeBFSIterator<>(tree, this), false);
    }

    public void printDFS(ST tree, int depth) {
        if (tree instanceof Fail) {
            System.out.println(repeat("    ", depth) + "- Fail");
        } else if (tree instanceof Exception) {
            System.out.println(repeat("    ", depth) + "- Exception " + ((Exception)tree).exception);
        } else if (tree instanceof Value) {
            System.out.println(repeat("    ", depth) + "- Value " + ((Value)tree).value);
        } else if (tree instanceof Choice) {
            
            System.out.println(repeat("    ", depth) + "- Choice ");
            printDFS(((Choice) tree).st1, depth + 1);
            printDFS(((Choice) tree).st2, depth + 1);
        } else if (tree instanceof STProxy) {
            if (((STProxy)tree).isEvaluated()) printDFS(((STProxy)tree).eval(this), depth);
            else
                System.out.println(repeat("    ", depth) + "- STProxy ");
        } else {
            throw new IllegalStateException("Unknown tree node type.");
        }
    }

    private String repeat(String s, int depth) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            result.append(s);
        }
        return result.toString();
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

    public Stack<TrailElement> getCurrentTrail() {
        Stack<TrailElement> trail = this.currentTrail;
        this.currentTrail = new Stack<>();
        return trail;
    }

    public void applyState(Stack<TrailElement> previousState) {
        this.restoringMode = true;
        for (TrailElement e : previousState) {
            if (e instanceof VariableChanged) {
                VariableChanged vc = (VariableChanged) e;
                this.setVar(vc.variable, vc.newValue);
            }
        }
        this.restoringMode = false;
    }

    public void revertState(Stack<TrailElement> previousState) {
        this.restoringMode = true;
        // TODO Reverse order.
        for (TrailElement e : previousState) {
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


