public class SimpleCoin  implements Program {
    public ST execute(STDemo vm, int pc) {
        switch (pc) {
            case 0:
                vm.setPC(0);
            case 1:
                vm.setPC(1);
                return new Choice(2, 4, "true", 1);
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
