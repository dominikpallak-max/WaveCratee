package pl.wavecode.crates;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class AnimationPlayer {

    private static final int FINALE = 32;
    private static final Random RNG = new Random();
    private static final Set<Run> RUNS = new HashSet<>();

    private AnimationPlayer() {
    }

    public static void start(WaveCrates plugin, Crate crate, Location block, Player player, ItemStack reward) {
        Run run = new Run(crate, block, player, reward);
        RUNS.add(run);
        run.runTaskTimer(plugin, 1L, 1L);
    }

    public static void finishAll() {
        for (Run run : new ArrayList<>(RUNS)) {
            run.finish();
        }
    }

    private static ItemStack one(ItemStack item) {
        ItemStack copy = item.clone();
        copy.setAmount(1);
        return copy;
    }

    private static void spin(ItemDisplay d, float scale, float angle) {
        d.setTransformation(new Transformation(
                new Vector3f(),
                new AxisAngle4f(angle, 0f, 1f, 0f),
                new Vector3f(scale, scale, scale),
                new AxisAngle4f()));
    }

    private static ItemDisplay spawn(World world, Location at, ItemStack item, boolean glow, float scale) {
        return world.spawn(at, ItemDisplay.class, d -> {
            d.setItemStack(item);
            d.setPersistent(false);
            d.setGlowing(glow);
            d.setGlowColorOverride(Color.fromRGB(85, 255, 255));
            d.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
            d.setTeleportDuration(2);
            d.setInterpolationDuration(2);
            spin(d, scale, 0f);
        });
    }

    private static Component nameOf(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.displayName();
        }
        return Component.translatable(item.getType().translationKey());
    }

    private static final class Run extends BukkitRunnable {

        private final Crate crate;
        private final Anim anim;
        private final World world;
        private final Location center;
        private final Player player;
        private final ItemStack reward;
        private final List<ItemStack> pool;
        private final List<ItemDisplay> items = new ArrayList<>();
        private BlockDisplay box;
        private ItemDisplay shown;
        private int t;
        private boolean done;

        Run(Crate crate, Location block, Player player, ItemStack reward) {
            this.crate = crate;
            this.anim = crate.anim;
            this.world = block.getWorld();
            this.center = block.clone().add(0.5, 0, 0.5);
            this.player = player;
            this.reward = reward;
            this.pool = new ArrayList<>(crate.drops);

            for (int i = 0; i < anim.count; i++) {
                items.add(spawn(world, center, one(randomPool()), crate.glowing, 0.9f));
            }

            Material mat = crate.material;
            if (mat.isBlock()) {
                box = world.spawn(block, BlockDisplay.class, d -> {
                    d.setBlock(mat.createBlockData());
                    d.setPersistent(false);
                    d.setTransformation(new Transformation(
                            new Vector3f(0.2f, 0f, 0.2f),
                            new AxisAngle4f(),
                            new Vector3f(0.6f, 0.6f, 0.6f),
                            new AxisAngle4f()));
                });
            }
        }

        private ItemStack randomPool() {
            return pool.get(RNG.nextInt(pool.size()));
        }

        @Override
        public void run() {
            try {
                step();
            } catch (Exception ex) {
                finish();
            }
        }

        private void step() {
            if (t < anim.ticks) {
                move();
            } else if (t == anim.ticks) {
                reveal();
            } else if (t < anim.ticks + FINALE) {
                rise();
            } else {
                finish();
                return;
            }
            t++;
        }

        private void move() {
            double p = t / (double) anim.ticks;
            for (int i = 0; i < items.size(); i++) {
                ItemDisplay d = items.get(i);
                double f = i / (double) anim.count;
                Vector o = anim.motion.at(f, i, anim.count, p);
                Location loc = center.clone().add(o);
                d.teleport(loc);
                spin(d, 0.9f, t * 0.25f + i);
                if (anim.cycle && t % (2 + (int) (p * 6)) == 0) {
                    d.setItemStack(one(randomPool()));
                }
                if (t % 2 == 0) {
                    world.spawnParticle(anim.particle, loc, 1, 0.1, 0.1, 0.1, 0);
                }
            }
            if (t % 4 == 0) {
                world.playSound(center, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, (float) (0.6 + 1.3 * p));
            }
        }

        private void reveal() {
            for (ItemDisplay d : items) {
                d.remove();
            }
            items.clear();
            shown = spawn(world, center.clone().add(0, 1.3, 0), reward, crate.glowing, 1.2f);
            world.playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.6f);
        }

        private void rise() {
            if (shown == null) {
                return;
            }
            double q = (t - anim.ticks) / (double) FINALE;
            Location loc = center.clone().add(0, 1.3 + q * 1.2, 0);
            shown.teleport(loc);
            spin(shown, (float) (1.2 + q * 1.0), t * (0.3f + (float) q));
            world.spawnParticle(Particle.END_ROD, loc, 2, 0.2, 0.2, 0.2, 0.01);
            if (t % 3 == 0) {
                world.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, (float) (1.0 + q));
            }
        }

        void finish() {
            if (done) {
                return;
            }
            done = true;
            try {
                cancel();
            } catch (IllegalStateException ignored) {
            }
            RUNS.remove(this);

            for (Entity e : items) {
                e.remove();
            }
            if (shown != null) {
                shown.remove();
            }
            if (box != null) {
                box.remove();
            }

            Location end = center.clone().add(0, 2.5, 0);
            world.spawnParticle(Particle.FIREWORK, end, 80, 0.4, 0.4, 0.4, 0.25);
            world.spawnParticle(Particle.TOTEM_OF_UNDYING, end, 50, 0.5, 0.5, 0.5, 0.4);
            world.spawnParticle(Particle.EXPLOSION, end, 1);
            world.playSound(end, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.5f, 1f);
            world.playSound(end, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);

            if (player.isOnline()) {
                for (ItemStack left : player.getInventory().addItem(reward.clone()).values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), left);
                }
                Component name = nameOf(reward).colorIfAbsent(NamedTextColor.GOLD);
                if (reward.getAmount() > 1) {
                    name = name.append(Msg.c(" &7x" + reward.getAmount()));
                }
                player.showTitle(Title.title(
                        name,
                        Msg.c("&7Wylosowano ze skrzynki &f" + crate.name),
                        Title.Times.times(Duration.ofMillis(300), Duration.ofSeconds(3), Duration.ofMillis(700))));
                Msg.send(player, "&aOtworzyłeś skrzynkę &f" + crate.name + "&a!");
            } else {
                world.dropItemNaturally(end, reward.clone());
            }
        }
    }
}
