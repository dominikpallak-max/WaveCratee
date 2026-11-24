package pl.wavecode.crates;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import static pl.wavecode.crates.M.TAU;
import static pl.wavecode.crates.M.c;
import static pl.wavecode.crates.M.s;
import static pl.wavecode.crates.M.sph;
import static pl.wavecode.crates.M.v;

public enum Anim {

    SPIRALA("Spirala", Material.NAUTILUS_SHELL, "Przedmioty wznoszą się w górę spiralą", 70, 12, Particle.END_ROD, false,
            (f, i, n, p) -> v(c(f * TAU + p * TAU * 3) * (1.3 - p * 0.7), 0.3 + p * 2.2, s(f * TAU + p * TAU * 3) * (1.3 - p * 0.7))),

    WYBUCH("Wybuch", Material.TNT, "Przedmioty eksplodują na wszystkie strony", 50, 16, Particle.FLAME, false,
            (f, i, n, p) -> {
                Vector d = sph(i, n);
                double r = 3.2 * Math.sqrt(p);
                return v(d.getX() * r, 1.2 + d.getY() * r - 2.0 * p * p, d.getZ() * r);
            }),

    AUTOMAT("Automat", Material.DISPENSER, "Losujące się bębny jak w jednorękim bandycie", 70, 3, Particle.CRIT, true,
            (f, i, n, p) -> v((i - (n - 1) / 2.0) * 0.95, 1.6 + 0.12 * s(p * 40 + i), 0)),

    HELISA("Helisa", Material.TWISTING_VINES, "Podwójna helisa pnąca się w górę", 70, 14, Particle.SOUL_FIRE_FLAME, false,
            (f, i, n, p) -> {
                double a = p * TAU * 2 + (i % 2) * Math.PI + f * 3;
                return v(c(a) * 0.9, 0.2 + f * 2.4, s(a) * 0.9);
            }),

    FONTANNA("Fontanna", Material.WATER_BUCKET, "Przedmioty tryskają w górę i opadają łukiem", 70, 12, Particle.BUBBLE_POP, false,
            (f, i, n, p) -> {
                double ph = (p * 2 + f) % 1;
                double a = i * 2.4;
                double r = ph * 1.7;
                return v(c(a) * r, 0.4 + 4 * ph * (1 - ph) * 2.2, s(a) * r);
            }),

    TORNADO("Tornado", Material.WIND_CHARGE, "Wirujący lej z przedmiotów", 70, 16, Particle.CLOUD, false,
            (f, i, n, p) -> {
                double a = p * TAU * 4 + f * TAU * 2;
                double r = 0.2 + f * 1.3;
                return v(c(a) * r, 0.1 + f * 2.8, s(a) * r);
            }),

    ORBITA("Orbita", Material.ENDER_EYE, "Przedmioty krążą wokół skrzynki", 70, 8, Particle.ENCHANT, false,
            (f, i, n, p) -> {
                double a = f * TAU + p * TAU * 2;
                return v(c(a) * 1.4, 1.3 + 0.3 * s(a * 2), s(a) * 1.4);
            }),

    DESZCZ("Deszcz", Material.LIGHT_BLUE_DYE, "Przedmioty spadają z nieba", 70, 12, Particle.HAPPY_VILLAGER, false,
            (f, i, n, p) -> {
                double ph = (p * 1.5 + f) % 1;
                double a = i * 2.4;
                double r = 0.3 + (i % 3) * 0.35;
                return v(c(a) * r, 3.6 - ph * 3.2, s(a) * r);
            }),

    WINDA("Winda", Material.PISTON, "Dwie kolumny przedmiotów jadą do góry", 60, 8, Particle.ELECTRIC_SPARK, false,
            (f, i, n, p) -> v(((i % 2) * 2 - 1) * 0.7, 0.2 + p * 2.0 + (i / 2) * 0.3, 0)),

    KOMETA("Kometa", Material.FIRE_CHARGE, "Przedmioty gonią się jak ogon komety", 70, 10, Particle.FLAME, false,
            (f, i, n, p) -> {
                double a = p * TAU * 3 - f * 1.4;
                return v(c(a) * 1.6, 1.2 + 0.5 * s(a * 2), s(a) * 1.6);
            }),

    FAJERWERK("Fajerwerk", Material.FIREWORK_ROCKET, "Wystrzał w górę i rozbłysk na niebie", 60, 14, Particle.FIREWORK, false,
            (f, i, n, p) -> {
                Vector d = sph(i, n);
                if (p < 0.5) {
                    return v(d.getX() * 0.2, 0.2 + p * 6.0, d.getZ() * 0.2);
                }
                double q = (p - 0.5) * 2;
                return v(d.getX() * q * 2.6, 3.2 + d.getY() * q * 2.0 - q * q * 1.5, d.getZ() * q * 2.6);
            }),

    WIR("Wir", Material.ENDER_PEARL, "Przedmioty spiralnie opadają do środka", 70, 12, Particle.PORTAL, false,
            (f, i, n, p) -> {
                double a = f * TAU + p * TAU * 4;
                double r = 2.2 * (1 - p) + 0.2;
                return v(c(a) * r, 0.3 + 2.2 * (1 - p), s(a) * r);
            }),

    PULSAR("Pulsar", Material.SEA_LANTERN, "Pulsująca kula przedmiotów", 70, 14, Particle.GLOW, false,
            (f, i, n, p) -> {
                Vector d = sph(i, n);
                double r = 0.8 + 0.5 * s(p * TAU * 4);
                return v(d.getX() * r, 1.4 + d.getY() * r, d.getZ() * r);
            }),

    FALA("Fala", Material.HEART_OF_THE_SEA, "Falująca linia przedmiotów", 70, 10, Particle.BUBBLE_POP, false,
            (f, i, n, p) -> {
                double x = (f - 0.5) * 3.2;
                double a = p * TAU;
                return v(x * c(a), 1.3 + 0.5 * s(p * TAU * 3 + f * TAU * 2), x * s(a));
            }),

    OSEMKA("Ósemka", Material.LEAD, "Przedmioty latają po torze ósemki", 70, 8, Particle.WAX_ON, false,
            (f, i, n, p) -> {
                double t = p * TAU * 2 + f * TAU;
                return v(1.5 * s(t), 1.3 + 0.25 * c(t * 3), 0.9 * s(2 * t));
            }),

    GWIAZDA("Gwiazda", Material.NETHER_STAR, "Obracająca się pięcioramienna gwiazda", 70, 15, Particle.END_ROD, false,
            (f, i, n, p) -> {
                double a = f * TAU + p * TAU;
                double r = 0.9 + 0.7 * c(5 * f * TAU);
                return v(c(a) * r, 1.3, s(a) * r);
            }),

    SERCE("Serce", Material.RED_DYE, "Przedmioty układają się w serce", 70, 16, Particle.HEART, false,
            (f, i, n, p) -> {
                double t = f * TAU;
                double x = 16 * Math.pow(s(t), 3) * 0.07;
                double y = (13 * c(t) - 5 * c(2 * t) - 2 * c(3 * t) - c(4 * t)) * 0.07;
                double a = p * TAU;
                return v(x * c(a), 1.5 + y, x * s(a));
            }),

    SZESCIAN("Sześcian", Material.SLIME_BLOCK, "Obracający się sześcian z przedmiotów", 70, 8, Particle.CRIT, false,
            (f, i, n, p) -> {
                double sx = (i & 1) * 2 - 1;
                double sy = ((i >> 1) & 1) * 2 - 1;
                double sz = ((i >> 2) & 1) * 2 - 1;
                double a = p * TAU * 2;
                double x = sx * 0.9;
                double z = sz * 0.9;
                return v(x * c(a) - z * s(a), 1.4 + sy * 0.9, x * s(a) + z * c(a));
            }),

    PIRAMIDA("Piramida", Material.SANDSTONE, "Obracająca się piramida", 70, 12, Particle.GLOW, false,
            (f, i, n, p) -> {
                double r = 1.5 * (1 - f) + 0.1;
                double a = f * TAU * 3 + p * TAU;
                return v(c(a) * r, 0.2 + f * 2.2 + p * 0.3, s(a) * r);
            }),

    BURZA("Burza", Material.LIGHTNING_ROD, "Chaotyczna burza z iskrami", 70, 12, Particle.ELECTRIC_SPARK, false,
            (f, i, n, p) -> {
                double r = 0.8 + 0.6 * s(p * 13 + i);
                return v(c(f * TAU + p * 7) * r, 1.2 + 0.8 * s(p * 11 + i * 2), s(f * TAU + p * 7) * r);
            }),

    PIORUN("Piorun", Material.TRIDENT, "Zygzakowata kolumna uderza w skrzynkę", 50, 10, Particle.ELECTRIC_SPARK, false,
            (f, i, n, p) -> v(s(f * 14) * 0.5 * (1 - p), 0.2 + f * 3.0 * (1 - p), c(f * 14) * 0.5 * (1 - p))),

    METEORYT("Meteoryt", Material.BLAZE_POWDER, "Przedmioty spadają z nieba jak meteory", 55, 8, Particle.FLAME, false,
            (f, i, n, p) -> {
                double a = f * TAU;
                double q = p * p;
                return v(c(a) * 4 * (1 - q), 1.2 + 3.3 * (1 - q), s(a) * 4 * (1 - q));
            }),

    PORTAL("Portal", Material.OBSIDIAN, "Obracający się pionowy portal", 70, 14, Particle.PORTAL, false,
            (f, i, n, p) -> {
                double a = f * TAU + p * TAU * 2;
                double x = c(a) * 1.2;
                double b = p * Math.PI;
                return v(x * c(b), 1.5 + s(a) * 1.2, x * s(b));
            }),

    GALAKTYKA("Galaktyka", Material.AMETHYST_SHARD, "Spiralne ramiona galaktyki", 75, 20, Particle.ENCHANT, false,
            (f, i, n, p) -> {
                double r = 0.3 + f * 1.7;
                double a = f * TAU * 2 + p * TAU;
                return v(c(a) * r, 1.3 + 0.15 * s(a * 3), s(a) * r);
            }),

    KARUZELA("Karuzela", Material.CLOCK, "Kręcąca się karuzela z przedmiotami", 70, 8, Particle.HAPPY_VILLAGER, false,
            (f, i, n, p) -> {
                double a = f * TAU + p * TAU * 2;
                return v(c(a) * 1.4, 1.0 + 0.5 * s(p * TAU * 3 + i * 1.5), s(a) * 1.4);
            }),

    SPREZYNA("Sprężyna", Material.STICKY_PISTON, "Przedmioty skaczą jak na sprężynie", 70, 14, Particle.WITCH, false,
            (f, i, n, p) -> {
                double a = f * TAU * 3 + p * TAU * 2;
                return v(c(a) * 0.8, 0.2 + f * (1.4 + 1.0 * s(p * TAU * 2)), s(a) * 0.8);
            }),

    KASKADA("Kaskada", Material.LAVA_BUCKET, "Przedmioty odbijają się jak piłki", 70, 10, Particle.BUBBLE_POP, false,
            (f, i, n, p) -> v((f - 0.5) * 2.6, 0.3 + 2.4 * Math.abs(s((p * 2 + f) * Math.PI)), s(i * 1.7) * 0.6)),

    HIPNOZA("Hipnoza", Material.FERMENTED_SPIDER_EYE, "Trzy kręgi kręcące się w przeciwne strony", 70, 12, Particle.SOUL, false,
            (f, i, n, p) -> {
                int ring = i % 3;
                int dir = ring % 2 == 0 ? 1 : -1;
                double a = (i / 3) * TAU / 4 + dir * p * TAU * 3;
                double r = 0.5 + ring * 0.5;
                return v(c(a) * r, 1.3, s(a) * r);
            }),

    CZARNA_DZIURA("Czarna dziura", Material.BLACK_DYE, "Przedmioty wsysane do środka", 70, 14, Particle.SQUID_INK, false,
            (f, i, n, p) -> {
                double a = f * TAU + p * TAU * 5;
                double r = 2.6 * (1 - p) * (1 - p) + 0.1;
                return v(c(a) * r, 1.3 + s(a * 2) * 0.2 * (1 - p), s(a) * r);
            }),

    SUPERNOWA("Supernowa", Material.BEACON, "Zapadnięcie do środka i potężny wybuch", 70, 16, Particle.TOTEM_OF_UNDYING, false,
            (f, i, n, p) -> {
                Vector d = sph(i, n);
                double r = p < 0.5 ? 1.8 * (1 - 2 * p) + 0.1 : 0.1 + 3.2 * (2 * p - 1);
                return v(d.getX() * r, 1.4 + d.getY() * r, d.getZ() * r);
            });

    public final String display;
    public final Material icon;
    public final String desc;
    public final int ticks;
    public final int count;
    public final Particle particle;
    public final boolean cycle;
    public final Motion motion;

    Anim(String display, Material icon, String desc, int ticks, int count, Particle particle, boolean cycle, Motion motion) {
        this.display = display;
        this.icon = icon;
        this.desc = desc;
        this.ticks = ticks;
        this.count = count;
        this.particle = particle;
        this.cycle = cycle;
        this.motion = motion;
    }

    public String id() {
        return name().toLowerCase();
    }

    public static Anim find(String raw) {
        if (raw == null) {
            return null;
        }
        String key = raw.trim().replace(' ', '_');
        for (Anim a : values()) {
            if (a.name().equalsIgnoreCase(key)) {
                return a;
            }
        }
        return null;
    }

    public static Anim from(String raw) {
        Anim a = find(raw);
        return a == null ? SPIRALA : a;
    }

    @FunctionalInterface
    public interface Motion {
        Vector at(double f, int i, int n, double p);
    }
}
