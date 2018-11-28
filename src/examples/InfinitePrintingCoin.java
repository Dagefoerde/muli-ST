package examples;

import searchtree.Choice;
import searchtree.ST;
import searchtree.Value;
import vm.VM;

/**
 * pc 0: boolean coin1 free, coin2 free;
 *    1: if (coin1) {
 *    2:     if (coin2) {
 *    3:         return recursion -> 1;
 *    4:     } else {
 *    5:         System.out.println("Hi");
 *    6:         return (coin1 || coin2);
 *    7:     }
 *    8: } else {
 *    9:     if (coin2) {
 *    10:         return (coin1 && coin2);
 *    11:    } else {
 *    12:       return (coin1 && true);
 *    13:    }
 *    14:}
 */
public class InfinitePrintingCoin implements Program {
    public ST execute(VM vm, int pc) {
        switch (pc) {
            case 0:
                vm.setPC(0);
            case 1:
                vm.setPC(1);
                return new Choice(2, 9, "true", vm.extractCurrentTrail(), vm.getCurrentChoice());
            case 2:
                vm.setPC(2);
                return new Choice(1, 5, "true", vm.extractCurrentTrail(), vm.getCurrentChoice());
            case 5:
                vm.setPC(5);
                vm.println("Hi");
            case 6:
                vm.setPC(6);
                return new Value("coin || coin2"); // True
            case 9:
                vm.setPC(9);
                return new Choice(10, 12, "true", vm.extractCurrentTrail(), vm.getCurrentChoice());
            case 10:
                vm.setPC(10);
                return new Value("coin && coin2"); // False
            case 12:
                vm.setPC(12);
                return new Value("coin && True"); // False
            default:
                throw new IllegalStateException();
        }
    }
}
