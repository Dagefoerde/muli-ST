import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class STDemo {
    public static void main(String[] args) {
        new STDemo();
    }

    private int pc;
    private Program program;

    public STDemo() {
        program = new SimpleCoin();
        ST<Object> tree = new UnevaluatedST<>(0);
        ST<Object> treeAfter = new Choice<Object>(2, 4, "coin",1);
        ((Choice)treeAfter).st1 = new Value<Object>("coin || False");
        ((Choice)treeAfter).st2 = new Value<Object>("coin && True");

        List<Object> leaves = walkDFS(tree);
        List<Object> leaves2 = walkDFS(treeAfter);
        System.out.print("Traversiert: ");
        Stream.of(leaves).forEach(System.out::println);
        System.out.print("Erwartet: ");
        Stream.of(leaves2).forEach(System.out::println);

        assert (tree.equals(treeAfter));
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

    public UnevaluatedST(int pc) {
        this.pc = pc;

    }

    public ST eval(STDemo vm) {
        if (evaluated != null) {
            return evaluated;
        }

        vm.setPC(this.pc);
        return vm.execute();
    }
}

