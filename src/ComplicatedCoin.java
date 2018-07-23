import searchtree.Choice;
import searchtree.ST;
import searchtree.Value;

/**
 * pc 0: boolean coin1 free, coin2 free;
 *    1: if (coin1) {
 *    2:     if (coin2) {
 *    3:         return (coin1 || false);
 *    4:     } else {
 *    5:         return (coin1 || coin2);
 *    6:     }
 *    7: } else {
 *    8:     if (coin2) {
 *    9:         return (coin1 && coin2);
 *    10:    } else {
 *    11:       return (coin1 && true);
 *    12:    }
 *    13:}
 */
public class ComplicatedCoin implements Program {
    public ST execute(STDemo vm, int pc) {
        switch (pc) {
            case 0:
                vm.setPC(0);
            case 1:
                vm.setPC(1);
                return new Choice(2, 8, "true", vm.getCurrentTrail(), vm.getCurrentChoice());
            case 2:
                vm.setPC(2);
                return new Choice(3, 5, "true", vm.getCurrentTrail(), vm.getCurrentChoice());
            case 3:
                vm.setPC(3);
                return new Value("coin || False"); // True
            case 5:
                vm.setPC(5);
                return new Value("coin || coin2"); // True
            case 8:
                vm.setPC(8);
                return new Choice(9, 11, "true", vm.getCurrentTrail(), vm.getCurrentChoice());
            case 9:
                vm.setPC(9);
                return new Value("coin && coin2"); // False
            case 11:
                vm.setPC(11);
                return new Value("coin && True"); // False
            default:
                throw new IllegalStateException();
        }
    }
}
