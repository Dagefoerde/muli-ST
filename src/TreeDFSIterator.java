import searchtree.*;

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
        // TODO add use of trail for backtracking/application of states.
        if (nodes.isEmpty()) {
            return false;
        }
        ST<T> tree = nodes.pop();
        if (tree instanceof Fail) {
            return tryAdvance(action);
        } else if (tree instanceof searchtree.Exception) {
            action.accept(null);
            return true;
        } else if (tree instanceof Value) {
            action.accept(((Value<T>)tree).value);
            return true;
        } else if (tree instanceof Choice) {
            // First push st2 so that st1 (and its children) will be popped first.
            nodes.push(((Choice<T>) tree).st2);
            nodes.push(((Choice<T>) tree).st1);
            return tryAdvance(action);
        } else if (tree instanceof STProxy) {
            STProxy<T> uneval = (STProxy<T>) tree;
            ST<T> eval = uneval.eval(this.vm);
            nodes.push(eval);
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

