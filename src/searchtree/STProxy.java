package searchtree;


import vm.VM;

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
}
