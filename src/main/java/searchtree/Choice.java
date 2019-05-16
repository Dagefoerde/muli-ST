package searchtree;

import trail.TrailElement;

import java.util.LinkedList;

public class Choice<A> extends ST<A> {
    public final Choice<A> parent;
    public final LinkedList<TrailElement> trail;
    public STProxy<A> st1;
    public STProxy<A> st2;
    private final String ce1;
    private final String ce2;

    public Choice(int pcNext, int pcWithJump, String constraintExpression, LinkedList<TrailElement> state, Choice<A> parent) {
        this.st1 = new STProxy<A>(pcNext, this);
        this.st2 = new STProxy<A>(pcWithJump, this);
        this.ce1 = constraintExpression;
        this.ce2 = "not " + constraintExpression;
        this.trail = state;
        this.parent = parent;
    }


}
