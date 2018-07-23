import searchtree.Choice;
import searchtree.ST;
import searchtree.Value;

/**
 * pc 0: boolean coin free;
 *    1: if (coin1) {
 *    2:    return (coin1 || false);
 *    3: } else {
 *    4:    return (coin1 && true);
 *    5: }
 */public class SimpleCoin  implements Program {
    public ST execute(STDemo vm, int pc) {
        switch (pc) {
            case 0:
                vm.setPC(0);
            case 1:
                vm.setPC(1);
                return new Choice(2, 4, "true", vm.getCurrentTrail(), vm.getCurrentChoice());
            case 2:
                vm.setPC(2);
                return new Value("coin || False"); // True
            case 4:
                vm.setPC(4);
                return new Value("coin && True"); // False
            default:
                throw new IllegalStateException();
        }
    }
}
