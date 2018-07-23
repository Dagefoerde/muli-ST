package searchtree;


import trail.TrailElement;
import vm.VM;

import java.util.LinkedList;

public class STProxy<A> extends ST<A> {
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

    public STProxy(int pc, Choice<A> childOf) {
        this.childOf = childOf;
        this.pc = pc;

    }

    public ST<A> eval(VM vm) {
        // TODO extract to VM!
        if (evaluated != null) {
            return evaluated;
        }

        vm.setPC(this.pc);
        this.evaluated = vm.execute();
        return this.evaluated;
    }

    /**
     * For a (proxy) node n with parents p_1, p_2, ..., p_i, r where r is root,
     * the trail of n is trail(r) ++ trail(p_i) ++ .. ++ trail(p_2) ++ trail(p1).
     * n does not have a local trail.
     * @return
     */
    public LinkedList<TrailElement> getTrail() {
        Choice<A> before = this.childOf;
        LinkedList<TrailElement> trail = new LinkedList<>();
        while (before != null) {
            trail.addAll(0, before.trail);
            before = before.parent;
        }
        return trail;
    }

    public Choice getParent() {
        return childOf;
    }
}
