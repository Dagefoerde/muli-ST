import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Consumer;

class TreeDFSIterator<T> implements Spliterator<T> {
    private final Stack<ST<T>> nodes;
    private final STDemo vm;

    public TreeDFSIterator(ST<T> st, STDemo vm) {
        nodes = new Stack<>();
        nodes.push(st);
        this.vm = vm;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (nodes.isEmpty()) {
            return false;
        }
        ST<T> tree = nodes.pop();
        if (tree instanceof Fail) {
            return tryAdvance(action);
        } else if (tree instanceof Exception) {
            action.accept(null);
            return true;
        } else if (tree instanceof Value) {
            action.accept(((Value<T>)tree).value);
            return true;
        } else if (tree instanceof Choice) {
            nodes.push(((Choice) tree).st2);
            nodes.push(((Choice) tree).st1);
            return tryAdvance(action);
        } else if (tree instanceof UnevaluatedST) {
            UnevaluatedST uneval = (UnevaluatedST) tree;
            nodes.push(uneval.eval(this.vm));
            return tryAdvance(action);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return 0;
    }
}
