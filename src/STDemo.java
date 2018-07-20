import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class STDemo {
    public static void main(String[] args) {
        new STDemo();
    }

    private int pc;
    private Program program;

    public STDemo() {
        //program = new SimpleCoin();
        //program = new ComplicatedCoin();
        program = new InfiniteCoin();
        ST<Object> tree = new UnevaluatedST<>(0);

        //List<Object> leaves = walkDFS(tree);
        List<Object> leaves = lazyBFS(tree).limit(6).collect(Collectors.toList());
        System.out.print("Traversiert: ");
        Stream.of(leaves).forEach(System.out::println);
        printDFS(tree, 0);
        //System.out.print("Erwartet: ");
        //Stream.of(leaves2).forEach(System.out::println);

        //assert (tree.equals(treeAfter));
    }

    public List walkDFS(ST tree) {
        if (tree instanceof Fail) {
            return Collections.EMPTY_LIST;
        } else if (tree instanceof Exception) {
            ArrayList l = new ArrayList();
            l.add(((Exception)tree).exception);
            return l;
        } else if (tree instanceof Value) {
            ArrayList l = new ArrayList();
            l.add(((Value)tree).value);
            return l;
        } else if (tree instanceof Choice) {
            List list = walkDFS(((Choice) tree).st1);
            list.addAll(walkDFS(((Choice) tree).st2));
            return list;
        } else if (tree instanceof UnevaluatedST) {
            UnevaluatedST uneval = (UnevaluatedST) tree;
            return walkDFS(uneval.eval(this));
        } else {
            throw new IllegalStateException();
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
            throw new IllegalStateException();
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
        System.out.println(pc);
    }
    public int getPC() {
        return this.pc;
    }

    public ST execute() {
        return program.execute(this, this.getPC());
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

    public ST<A> st1;
    public ST<A> st2;
    private final String ce1;
    private final String ce2;
    public int state;

    public Choice(int pcNext, int pcWithJump, String constraintExpression, int state) {
        this.st1 = new UnevaluatedST<A>(pcNext);
        this.st2 = new UnevaluatedST<A>(pcWithJump);
        this.ce1 = constraintExpression;
        this.ce2 = "not " + constraintExpression;
        this.state = state;
    }


}

class UnevaluatedST<A> extends ST<A> {
    private final int pc;

    private ST evaluated = null;

    public boolean isEvaluated() {
        return this.evaluated != null;
    }

    public UnevaluatedST(int pc) {
        this.pc = pc;

    }

    public ST eval(STDemo vm) {
        if (evaluated != null) {
            return evaluated;
        }

        vm.setPC(this.pc);
        this.evaluated = vm.execute();
        return this.evaluated;
    }
}

