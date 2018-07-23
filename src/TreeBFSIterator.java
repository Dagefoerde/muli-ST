import searchtree.*;
import trail.TrailElement;

import java.util.LinkedList;
import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Consumer;

class TreeBFSIterator<T> implements Spliterator<T> {
    private final LinkedList<ST<T>> queue;
    private final STDemo vm;

    public TreeBFSIterator(ST<T> st, STDemo vm) {
        queue = new LinkedList<>();
        queue.add(st);
        this.vm = vm;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        // TODO add use of trail for backtracking/application of states.
        if (queue.isEmpty()) {
            return false;
        }
        ST<T> tree = queue.remove();
        if (tree instanceof Fail) {
            return tryAdvance(action);
        } else if (tree instanceof searchtree.Exception) {
            action.accept(null);
            return true;
        } else if (tree instanceof Value) {
            action.accept(((Value<T>)tree).value);
            return true;
        } else if (tree instanceof Choice) {
            queue.add(((Choice<T>) tree).st1);
            queue.add(((Choice<T>) tree).st2);
            return tryAdvance(action);
        } else if (tree instanceof STProxy) {
            STProxy<T> uneval = (STProxy<T>) tree;
            // Compute trail for restoring state.
            Stack<TrailElement> trail = uneval.getTrail();
            // Apply VM state.
            this.vm.applyState(trail);
            Choice<T> previousChoice = this.vm.getCurrentChoice();
            this.vm.setCurrentChoice(uneval.getParent());
            // Evaluate subtree.
            ST result = uneval.eval(this.vm);
            // Revert to previous state.
            this.vm.revertState(this.vm.getCurrentTrail());
            this.vm.revertState(trail);
            this.vm.setCurrentChoice(previousChoice);
            if (result instanceof Choice) {
                queue.add(result);
            } else {
                queue.addFirst(result);
            }
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
