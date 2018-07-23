package searchtree;

import trail.TrailElement;

import java.util.Stack;

public class Choice<A> extends ST<A> {

    public final Stack<TrailElement> trail;
    public ST<A> st1;
    public ST<A> st2;
    private final String ce1;
    private final String ce2;

    public Choice(int pcNext, int pcWithJump, String constraintExpression, Stack<TrailElement> state) {
        this.st1 = new STProxy<A>(pcNext, this);
        this.st2 = new STProxy<A>(pcWithJump, this);
        this.ce1 = constraintExpression;
        this.ce2 = "not " + constraintExpression;
        this.trail = state;
    }


}
