import searchtree.Choice;
import searchtree.ST;
import searchtree.Value;

/**
 * pc 0: boolean coin free, coin2 free;
 *    1: int i = 1;
 *    2: if (coin) {
 *    3:    -> 1
 *    4: } else {
 *    5:    int j = 2;
 *    6:    if (coin2) {
 *    7:        j = 0;
 *    8:    } else {
 *    9:        i = j+1;
 *    10:   }
 *    11:
 *    12:}
 *    13:return i;
 */
public class InfiniteComplicatedAssignmentCoin implements Program {
    public ST execute(STDemo vm, int pc) {
        switch (pc) {
            case 0:
                vm.setPC(0);
            case 1:
                vm.setPC(1);
                vm.setVar("i", 1);
            case 2:
                vm.setPC(2);
                return new Choice(1, 5, "coin == true", vm.getCurrentTrail(), vm.getCurrentChoice());
            case 3:
                vm.setPC(3);
                vm.setVar("i", 0);
                return execute(vm, 13);
            case 5:
                vm.setPC(5);
                vm.setVar("j", 2);
            case 6:
                vm.setPC(6);
                return new Choice(7, 9, "coin2 == true", vm.getCurrentTrail(), vm.getCurrentChoice());
            case 7:
                vm.setPC(7);
                vm.setVar("j", 0);
                return execute(vm, 13);
            case 9:
                vm.setPC(5);
                vm.setVar("i", vm.getVar("j") + 1);
            case 13:
                vm.setPC(13);
                return new Value(vm.getVar("i"));
            default:
                throw new IllegalStateException();
        }
    }
}
