package examples;

import searchtree.ST;
import vm.VM;

public interface Program {
    ST execute(VM vm, int pc);
}
