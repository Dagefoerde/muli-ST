package trail;

import java.util.Optional;

public class VariableChanged implements TrailElement {
    public final String variable;
    public final Optional<Integer> formerValue;
    public final int newValue;
    public VariableChanged(String variable, Optional<Integer> formerValue, int newValue) {
        this.variable = variable;
        this.formerValue = formerValue;
        this.newValue = newValue;
    }
}