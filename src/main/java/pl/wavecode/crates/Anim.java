package pl.wavecode.crates;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import static pl.wavecode.crates.M.TAU;
import static pl.wavecode.crates.M.c;
import static pl.wavecode.crates.M.cl;
import static pl.wavecode.crates.M.ease;
import static pl.wavecode.crates.M.lerp;
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
            }),

    KROPLA("Kropla", Material.PRISMARINE_CRYSTALS, "Przedmioty spadają kroplami do środka", 70, 12, Particle.DRIPPING_WATER, false,
            (f, i, n, p) -> {
                double q = ease(cl(p * 1.5 - f * 0.5));
                double a = f * TAU;
                return v(c(a) * 0.9 * (1 - q), 3.8 - 2.5 * q, s(a) * 0.9 * (1 - q));
            }),

    LOTOS("Lotos", Material.PINK_PETALS, "Rozkwitający kwiat lotosu", 75, 12, Particle.CHERRY_LEAVES, false,
            (f, i, n, p) -> {
                int layer = i % 3;
                double open = ease(p * 1.6);
                double a = (i / 3) * TAU / 4 + layer * 0.5 + p * TAU * 0.5;
                double r = (0.4 + layer * 0.5) * open;
                return v(c(a) * r, 0.9 + layer * 0.3 + 0.3 * s(p * TAU * 2), s(a) * r);
            }),

    DNA("DNA", Material.VINE, "Potrójna nić pnąca się w górę", 75, 15, Particle.ENCHANT, false,
            (f, i, n, p) -> {
                double a = p * TAU * 2 + (i % 3) * TAU / 3 + f * 4;
                double r = 0.8 + 0.2 * s(f * 10);
                return v(c(a) * r, 0.2 + f * 2.6, s(a) * r);
            }),

    ZEGAR("Zegar", Material.COMPASS, "Wskazówki zegara odliczają do otwarcia", 70, 10, Particle.END_ROD, false,
            (f, i, n, p) -> {
                double ang = i % 2 == 0 ? p * TAU * 4 : p * TAU * 0.5;
                double r = 0.3 + (i / 2) * 0.35;
                double b = p * Math.PI * 0.5;
                double x = c(ang) * r;
                return v(x * c(b), 1.6 + s(ang) * r, x * s(b));
            }),

    MOTYL("Motyl", Material.FEATHER, "Machające skrzydła motyla", 80, 24, Particle.WITCH, false,
            (f, i, n, p) -> {
                double t = f * 12 * Math.PI;
                double k = Math.exp(c(t)) - 2 * c(4 * t) - Math.pow(s(t / 12), 5);
                double flap = 0.6 + 0.4 * s(p * TAU * 6);
                double x = s(t) * k * 0.35 * flap;
                double y = c(t) * k * 0.35;
                double a = p * TAU;
                return v(x * c(a), 1.5 + y, x * s(a));
            }),

    WAZ("Wąż", Material.SLIME_BALL, "Wąż z przedmiotów pełza w powietrzu", 80, 10, Particle.SPORE_BLOSSOM_AIR, false,
            (f, i, n, p) -> {
                double u = p * TAU * 1.5 - f * 0.9;
                return v(1.5 * s(3 * u), 1.4 + 0.7 * s(2 * u), 1.5 * s(2 * u + 1));
            }),

    KOLO_FORTUNY("Koło fortuny", Material.GOLD_INGOT, "Pionowe koło, które zwalnia przed losowaniem", 80, 10, Particle.CRIT, false,
            (f, i, n, p) -> {
                double a = f * TAU + ease(p) * TAU * 3;
                double x = c(a) * 1.3;
                return v(x * c(0.5), 1.6 + s(a) * 1.3, x * s(0.5));
            }),

    RULETKA("Ruletka", Material.REDSTONE, "Dwa koła ruletki kręcą się w przeciwne strony", 80, 12, Particle.ELECTRIC_SPARK, true,
            (f, i, n, p) -> {
                int ring = i % 2;
                double dir = ring == 0 ? 1 : -1;
                double a = (i / 2) * TAU / (n / 2.0) + dir * ease(p) * TAU * 4;
                double r = 1.0 + ring * 0.7;
                return v(c(a) * r, 1.2, s(a) * r);
            }),

    SKRZYDLA("Skrzydła", Material.ELYTRA, "Machające skrzydła z przedmiotów", 75, 14, Particle.END_ROD, false,
            (f, i, n, p) -> {
                double side = i % 2 == 0 ? 1 : -1;
                double t = ((i / 2) + 1) / (n / 2.0);
                double flap = s(p * TAU * 3) * 0.5;
                double x = side * (0.3 + t * 1.8);
                double y = 1.2 + t * 0.9 * (0.5 + flap) - t * t * 0.3;
                double z = -0.3 * t;
                double a = p * TAU;
                return v(x * c(a) - z * s(a), y, x * s(a) + z * c(a));
            }),

    KORONA("Korona", Material.GOLDEN_HELMET, "Korona wynurza się i wiruje", 70, 12, Particle.TOTEM_OF_UNDYING, false,
            (f, i, n, p) -> {
                double a = f * TAU + p * TAU;
                double up = 1 - ease(p * 1.5);
                return v(c(a) * 1.1, 1.8 + (i % 2) * 0.5 + 0.1 * s(p * TAU * 4) - up * 1.5, s(a) * 1.1);
            }),

    PROMIENIE("Promienie", Material.END_ROD, "Pulsujące promienie światła", 70, 18, Particle.FIREWORK, false,
            (f, i, n, p) -> {
                int ray = i / 3;
                int k = i % 3;
                double a = ray * TAU / 6 + p * TAU;
                double r = (k + 1) * 0.6 * (0.7 + 0.3 * s(p * TAU * 4));
                return v(c(a) * r, 1.3, s(a) * r);
            }),

    DYSKOTEKA("Dyskoteka", Material.NOTE_BLOCK, "Skaczące przedmioty na parkiecie", 70, 9, Particle.HAPPY_VILLAGER, false,
            (f, i, n, p) -> {
                double gx = (i % 3 - 1) * 0.9;
                double gz = ((i / 3) % 3 - 1) * 0.9;
                double a = p * TAU;
                return v(gx * c(a) - gz * s(a), 1.0 + 0.7 * Math.abs(s(p * TAU * 3 + i)), gx * s(a) + gz * c(a));
            }),

    RAKIETA("Rakieta", Material.FIREWORK_STAR, "Kolumna przedmiotów startuje jak rakieta", 60, 6, Particle.FLAME, false,
            (f, i, n, p) -> v(0.1 * s(p * 40 + i), 0.2 + p * p * 3.2 + i * 0.25, 0.1 * c(p * 40 + i))),

    SNIEZYCA("Śnieżyca", Material.SNOWBALL, "Wirująca śnieżyca z przedmiotami", 75, 14, Particle.SNOWFLAKE, false,
            (f, i, n, p) -> {
                double ph = (p * 1.2 + f) % 1;
                double a = f * TAU * 2 + p * TAU * 2;
                double r = 1.6 * (1 - ph * 0.5);
                return v(c(a) * r, 3.5 - ph * 3.0, s(a) * r);
            }),

    ZLOTY_PYL("Złoty pył", Material.GLOWSTONE_DUST, "Przedmioty latają jak magiczny pył", 75, 14, Particle.WAX_ON, false,
            (f, i, n, p) -> {
                double a = f * TAU + p * 8 + s(p * 17 + i);
                double r = 0.7 + 0.6 * s(p * 9 + i * 2);
                return v(c(a) * r, 1.3 + 0.7 * s(p * 7 + i * 3), s(a) * r);
            }),

    TROJKAT("Trójkąt", Material.QUARTZ, "Przedmioty biegną po bokach trójkąta", 70, 12, Particle.SOUL_FIRE_FLAME, false,
            (f, i, n, p) -> {
                double u = ((f + p * 2) % 1) * 3;
                int seg = (int) u;
                double l = u - seg;
                double a0 = seg * TAU / 3;
                double a1 = (seg + 1) * TAU / 3;
                return v(lerp(c(a0), c(a1), l) * 1.6, 1.3, lerp(s(a0), s(a1), l) * 1.6);
            }),

    PENTAGRAM("Pentagram", Material.REDSTONE_TORCH, "Przedmioty rysują pentagram", 80, 15, Particle.SOUL_FIRE_FLAME, false,
            (f, i, n, p) -> {
                double u = ((f + p) % 1) * 5;
                int seg = (int) u;
                double l = u - seg;
                double a0 = seg * 2 * TAU / 5;
                double a1 = (seg + 1) * 2 * TAU / 5;
                return v(lerp(c(a0), c(a1), l) * 1.6, 1.3, lerp(s(a0), s(a1), l) * 1.6);
            }),

    GIROSKOP("Giroskop", Material.SPYGLASS, "Trzy obracające się pierścienie", 75, 12, Particle.ENCHANT, false,
            (f, i, n, p) -> {
                int ring = i % 3;
                double a = (i / 3) * TAU / 4 + p * TAU * (1 + ring * 0.5);
                double b = p * TAU;
                double x0 = c(a) * 1.3;
                double y0 = s(a) * 1.3;
                if (ring == 0) {
                    return v(x0 * c(b), 1.5 + y0, x0 * s(b));
                }
                if (ring == 1) {
                    return v(x0, 1.5 + y0 * c(b), y0 * s(b));
                }
                return v(x0, 1.5 + 0.3 * s(b * 2), y0);
            }),

    WULKAN("Wulkan", Material.MAGMA_BLOCK, "Erupcja lawy z przedmiotami", 70, 14, Particle.LAVA, false,
            (f, i, n, p) -> {
                double ph = (p * 2.5 + f) % 1;
                double a = i * 2.4;
                double r = ph * 1.2 * (0.5 + (i % 3) * 0.4);
                return v(c(a) * r, 0.2 + 4 * ph * (1 - ph) * 3.2, s(a) * r);
            }),

    TRZESIENIE("Trzęsienie", Material.ANVIL, "Ziemia drży, a przedmioty podskakują", 70, 10, Particle.POOF, false,
            (f, i, n, p) -> {
                double a = f * TAU;
                double jump = p > 0.8 ? 3.0 * Math.sin((p - 0.8) / 0.2 * Math.PI * 0.5) : 0;
                return v(c(a) * 1.1 + 0.15 * s(p * 90 + i * 2), 0.3 + jump + 0.1 * Math.abs(s(p * 60 + i)), s(a) * 1.1 + 0.15 * c(p * 85 + i));
            }),

    KONSTELACJA("Konstelacja", Material.ECHO_SHARD, "Gwiazdozbiór mrugający nad skrzynką", 80, 12, Particle.END_ROD, false,
            (f, i, n, p) -> {
                Vector d = sph(i, n);
                double a = p * TAU * 0.7;
                double r = 1.8 + 0.2 * s(p * TAU * 3 + i);
                double x = d.getX() * r;
                double z = d.getZ() * r;
                return v(x * c(a) - z * s(a), 2.2 + d.getY() * r, x * s(a) + z * c(a));
            }),

    TELEPORT("Teleport", Material.CHORUS_FRUIT, "Przedmioty teleportują się w losowe miejsca", 70, 10, Particle.PORTAL, false,
            (f, i, n, p) -> {
                int idx = (int) (p * 8);
                double a = Math.toRadians((i * 73 + idx * 131) % 360);
                double r = 0.8 + ((i * 37 + idx * 17) % 10) * 0.1;
                return v(c(a) * r, 0.6 + ((i * 53 + idx * 29) % 20) * 0.1, s(a) * r);
            }),

    WSTEGA("Wstęga", Material.STRING, "Wstęga Möbiusa z przedmiotów", 75, 16, Particle.GLOW, false,
            (f, i, n, p) -> {
                double t = f * TAU + p * TAU;
                double half = t / 2;
                double w = (i % 2 * 2 - 1) * 0.4;
                double rr = 1.3 + w * c(half);
                return v(rr * c(t), 1.4 + w * s(half), rr * s(t));
            }),

    ATOM("Atom", Material.NETHERITE_INGOT, "Elektrony krążą po trzech orbitach", 75, 9, Particle.ELECTRIC_SPARK, false,
            (f, i, n, p) -> {
                int k = i % 3;
                double a = (i / 3) * TAU / 3 + p * TAU * 3;
                double x0 = c(a) * 1.4;
                double y0 = s(a) * 1.4;
                double y1 = y0 * c(1.05);
                double z1 = y0 * s(1.05);
                double rot = k * TAU / 3;
                return v(x0 * c(rot) + z1 * s(rot), 1.5 + y1, -x0 * s(rot) + z1 * c(rot));
            }),

    KWIAT("Kwiat", Material.RED_TULIP, "Przedmioty układają się w płatki kwiatu", 75, 24, Particle.CHERRY_LEAVES, false,
            (f, i, n, p) -> {
                double th = f * TAU;
                double r = 1.5 * c(3 * th);
                double a = th + p * TAU;
                return v(c(a) * r, 1.3 + 0.2 * s(p * TAU * 3), s(a) * r);
            }),

    TECZA("Tęcza", Material.AMETHYST_CLUSTER, "Kolorowa tęcza z łuków przedmiotów", 75, 20, Particle.HAPPY_VILLAGER, false,
            (f, i, n, p) -> {
                int ring = i % 4;
                int idx = i / 4;
                double a = idx * Math.PI / 4;
                double r = (1.0 + ring * 0.25) * ease(p * 1.5);
                double b = p * Math.PI;
                double x = c(a) * r;
                return v(x * c(b), 0.3 + s(a) * r * 1.2, x * s(b));
            }),

    BRAMA("Brama", Material.CRYING_OBSIDIAN, "Dwie świetliste bramy otwierają się nad skrzynką", 75, 14, Particle.PORTAL, false,
            (f, i, n, p) -> {
                double a = Math.PI * f;
                double r = 1.5 * cl(p * 1.5);
                double b = p * Math.PI * 0.5 + (i % 2) * Math.PI / 2;
                double x = c(a) * r;
                return v(x * c(b), 0.2 + s(a) * r * 1.4, x * s(b));
            }),

    WIATRAK("Wiatrak", Material.WHEAT, "Wirujące łopaty wiatraka", 70, 12, Particle.CLOUD, false,
            (f, i, n, p) -> {
                int blade = i % 4;
                int k = i / 4;
                double ang = blade * TAU / 4 + p * TAU * 3;
                double r = (k + 1) * 0.5;
                double b = p * Math.PI;
                double x = c(ang) * r;
                return v(x * c(b), 1.5 + s(ang) * r, x * s(b));
            }),
    WIELKI_WYBUCH("Wielki wybuch", Material.END_CRYSTAL, "Gigantyczna eksplozja kosmicznej energii", 70, 20, Particle.FIREWORK, false,
            (f, i, n, p) -> {
                Vector d = sph(i, n);
                double r = 3.5 * Math.pow(p, 0.4);
                double a = p * TAU;
                double x = d.getX() * r;
                double z = d.getZ() * r;
                return v(x * c(a) - z * s(a), 1.4 + d.getY() * r, x * s(a) + z * c(a));
            }),

    ROJ("Rój", Material.HONEYCOMB, "Rój przedmiotów krąży i wznosi się", 75, 14, Particle.WAX_ON, false,
            (f, i, n, p) -> {
                double a = p * TAU * 8 + i * 2;
                double cx = c(f * TAU) * 1.2;
                double cz = s(f * TAU) * 1.2;
                return v(cx + c(a) * 0.25, 1.0 + p * 1.5 + 0.2 * s(a * 2), cz + s(a) * 0.25);
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

    public boolean thunder() {
        return this == PIORUN || this == BURZA || this == SUPERNOWA || this == WIELKI_WYBUCH
                || this == METEORYT || this == WULKAN || this == TRZESIENIE;
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
