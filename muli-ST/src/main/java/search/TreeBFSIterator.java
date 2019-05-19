package search;

import searchtree.*;
import searchtree.Exception;
import trail.TrailElement;
import vm.VM;

import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TreeBFSIterator<T> implements Spliterator<T> {
    private final LinkedList<ST<T>> queue;
    private final VM vm;

    public TreeBFSIterator(ST<T> st, VM vm) {
        queue = new LinkedList<>();
        queue.add(st);
        this.vm = vm;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (queue.isEmpty()) {
            return false;
        }
        ST<T> tree = queue.remove();
        if (tree instanceof Fail) {
            return tryAdvance(action);
        } else if (tree instanceof Exception) {
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
            ST result;
            if (!uneval.isEvaluated()) {
                // Compute trail for restoring state.
                LinkedList<TrailElement> trail = uneval.getTrail();
                // Apply VM state.
                this.vm.applyState(trail);
                Choice<T> previousChoice = this.vm.getCurrentChoice();
                this.vm.setCurrentChoice(uneval.getParent());
                // Evaluate subtree.
                result = uneval.eval(this.vm);
                // Revert to initial state.
                this.vm.revertState(this.vm.extractCurrentTrail());
                if (result instanceof Choice) {
                    this.vm.revertState(((Choice) result).trail);
                }
                this.vm.revertState(trail);
                this.vm.setCurrentChoice(previousChoice);
            } else {
                result = uneval.eval(this.vm);
            }
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

    public static <A> Stream<A> stream(ST<A> tree, VM vm) {
        return StreamSupport.stream(new TreeBFSIterator<A>(tree, vm), false);
    }
}
