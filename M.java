package pl.wavecode.crates;

import org.bukkit.util.Vector;

public final class M {

    public static final double TAU = Math.PI * 2;

    private M() {
    }

    public static double c(double a) {
        return Math.cos(a);
    }

    public static double s(double a) {
        return Math.sin(a);
    }

    public static Vector v(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    public static Vector sph(int i, int n) {
        double y = 1 - 2 * (i + 0.5) / n;
        double r = Math.sqrt(Math.max(0, 1 - y * y));
        double a = i * 2.399963;
        return new Vector(Math.cos(a) * r, y, Math.sin(a) * r);
    }
}
