package vm;

import searchtree.ST;

public interface VM {
    void setPC(int pc);

    <A> ST<A> execute();
}
