import trail.TrailElement;
import trail.VariableChanged;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class STDemo {
    private boolean restoringMode = false;

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
        program = new AssignmentCoin();

        // Init execution.
        heap = new HashMap<>();
        currentTrail = new Stack<>();
        ST<Object> tree = new UnevaluatedST<>(0, null);

        List<Object> leaves = strictDFS(tree);
        //List<Object> leaves = lazyDFS(tree).limit(6).collect(Collectors.toList());
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
        } else if (tree instanceof UnevaluatedST) {
            UnevaluatedST uneval = (UnevaluatedST) tree;
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
        } else if (tree instanceof UnevaluatedST) {
            if (((UnevaluatedST)tree).isEvaluated()) printDFS(((UnevaluatedST)tree).eval(this), depth);
            else
                System.out.println(repeat("    ", depth) + "- UnevaluatedST ");
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

    public void setPC(int pc) {
        this.pc = pc;
    }

    public int getPC() {
        return this.pc;
    }

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
}

abstract class ST<A> {

}



class Fail extends ST {}

class Exception extends ST {
    public Throwable exception;
}

class Value<A> extends ST<A> {
    public A value;

    public Value(A s) {
        this.value = s;
    }
}

class Choice<A> extends ST<A> {

    public final Stack<TrailElement> trail;
    public ST<A> st1;
    public ST<A> st2;
    private final String ce1;
    private final String ce2;

    public Choice(int pcNext, int pcWithJump, String constraintExpression, Stack<TrailElement> state) {
        this.st1 = new UnevaluatedST<A>(pcNext, this);
        this.st2 = new UnevaluatedST<A>(pcWithJump, this);
        this.ce1 = constraintExpression;
        this.ce2 = "not " + constraintExpression;
        this.trail = state;
    }


}

class UnevaluatedST<A> extends ST<A> {
    /**
     * PC at which execution has to continue for evaluation.
     */
    private final int pc;
    /**
     * Records the direct parent in order to be able to obtain its trail if needed.
     */
    private final Choice<A> childOf;

    private ST<A> evaluated = null;

    public boolean isEvaluated() {
        return this.evaluated != null;
    }

    public UnevaluatedST(int pc, Choice<A> childOf) {
        this.childOf = childOf;
        this.pc = pc;

    }

    public ST<A> eval(STDemo vm) {
        if (evaluated != null) {
            return evaluated;
        }

        vm.setPC(this.pc);
        this.evaluated = vm.execute();
        return this.evaluated;
    }
}

