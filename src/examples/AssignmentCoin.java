package examples;

import searchtree.Choice;
import searchtree.ST;
import searchtree.Value;
import vm.VM;

/**
 * pc 0: boolean coin free;
 *    1: int i = 1;
 *    2: if (coin) {
 *    3:    i = 0;
 *    4:    int j = 2;
 *    5: } else {
 *    6:    // no-op (expect i == 1 and j erased).
 *    7: }
 *    8: return i;
 */
public class AssignmentCoin implements Program {
    public ST execute(VM vm, int pc) {
        switch (pc) {
            case 0:
                vm.setPC(0);
            case 1:
                vm.setPC(1);
                vm.setVar("i", 1);
            case 2:
                vm.setPC(2);
                return new Choice(3, 6, "true", vm.getCurrentTrail(), vm.getCurrentChoice());
            case 3:
                vm.setPC(3);
                vm.setVar("i", 0);
            case 4:
                vm.setPC(4);
                vm.setVar("j", 2);
                return execute(vm, 8);
            case 6:
                vm.setPC(6);
            case 8:
                vm.setPC(8);
                return new Value(vm.getVar("i"));
            default:
                throw new IllegalStateException();
        }
    }
}
