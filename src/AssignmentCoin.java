/**
 * pc 0: boolean coin free;
 *    1: int i = 1;
 *    2: if (coin) {
 *    3:    i = 0;
 *    4: } else {
 *    6: }
 *    7: return i;
 */
public class AssignmentCoin implements Program {
    public ST execute(STDemo vm, int pc) {
        switch (pc) {
            case 0:
                vm.setPC(0);
            case 1:
                vm.setPC(1);
                vm.setVar("i", 1);
            case 2:
                vm.setPC(2);
                return new Choice(3, 5, "true", 2);
            case 3:
                vm.setPC(3);
                vm.setVar("i", 0);
                return execute(vm, 7);
            case 5:
                vm.setPC(5);
            //    vm.setVar("i", 1);
            case 7:
                vm.setPC(7);
                return new Value(vm.getVar("i"));
            default:
                throw new IllegalStateException();
        }
    }
}
