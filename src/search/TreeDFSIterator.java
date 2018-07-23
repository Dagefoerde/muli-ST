package search;

import searchtree.*;
import trail.TrailElement;
import vm.VM;

import java.util.LinkedList;
import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TreeDFSIterator<T> implements Spliterator<T> {
    private final Stack<ST<T>> nodes;
    private final VM vm;

    public TreeDFSIterator(ST<T> st, VM vm) {
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
                // Revert to previous state.
                this.vm.revertState(this.vm.getCurrentTrail());
                this.vm.revertState(trail);
                this.vm.setCurrentChoice(previousChoice);
            } else {
                result = uneval.eval(this.vm);
            }
            nodes.push(result);
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
        return StreamSupport.stream(new TreeDFSIterator<A>(tree, vm), false);
    }
}

