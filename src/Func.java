import java.util.function.Function;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public enum Func {
    sinx("sin(x)", (x) -> sin(x)),
    sin2x("sin(2x)", (x) -> sin(2 * x)),
    sinxX("sin(x)*x", (x) -> sin(x) * x),
    cos2xX("cox(2*x)*x", (x) -> cos(2 * x) * x),
    x2("x*x", (x) -> x * x),
    sqrtx("sqrt(x)", (x) -> {
        return (x < 0) ? 0 : Math.sqrt(x);
    }),
    ;

    private final String label;
    private final Function<Double, Double> funct;

    Func(String label, Function<Double, Double> funct) {
        this.label = label;
        this.funct = funct;
    }

    public static Func labelOf(String label) {
        for (Func value : values()) {
            if (value.getLabel().equals(label)) {
                return value;
            }
        }
        return null;
    }

    public String getLabel() {
        return label;
    }

    public Function<Double, Double> getFunct() {
        return funct;
    }
}
