package pl.wavecode.crates;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrateManager {

    private final WaveCrates plugin;
    private final File file;
    private final Map<String, Crate> crates = new LinkedHashMap<>();
    private final NamespacedKey key;

    public CrateManager(WaveCrates plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "skrzynki.yml");
        this.key = new NamespacedKey(plugin, "skrzynka");
        plugin.getDataFolder().mkdirs();
    }

    public void load() {
        crates.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.getConfigurationSection("skrzynki");
        if (root == null) {
            return;
        }
        for (String id : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(id);
            if (s == null) {
                continue;
            }
            Material material = Material.matchMaterial(s.getString("blok", "CHEST"));
            if (material == null) {
                material = Material.CHEST;
            }
            Crate c = new Crate(id, material);
            c.name = s.getString("nazwa", c.name);
            c.lore = new ArrayList<>(s.getStringList("lore"));
            c.anim = Anim.from(s.getString("animacja"));
            c.glowing = s.getBoolean("glow", true);
            c.enabled = s.getBoolean("wlaczona", true);
            c.model = s.getInt("model", 0);
            List<?> raw = s.getList("drop", new ArrayList<>());
            if (raw != null) {
                for (Object o : raw) {
                    if (o instanceof ItemStack stack) {
                        c.drops.add(stack);
                    }
                }
            }
            crates.put(id.toLowerCase(), c);
        }
    }

    public void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Crate c : crates.values()) {
            String p = "skrzynki." + c.id + ".";
            cfg.set(p + "nazwa", c.name);
            cfg.set(p + "lore", c.lore);
            cfg.set(p + "blok", c.material.name());
            cfg.set(p + "animacja", c.anim.name());
            cfg.set(p + "glow", c.glowing);
            cfg.set(p + "wlaczona", c.enabled);
            cfg.set(p + "model", c.model);
            cfg.set(p + "drop", c.drops);
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Nie udało się zapisać skrzynek: " + e.getMessage());
        }
    }

    public Crate get(String id) {
        return id == null ? null : crates.get(id.toLowerCase());
    }

    public Collection<Crate> all() {
        return crates.values();
    }

    public List<String> ids() {
        return new ArrayList<>(crates.keySet());
    }

    public Crate create(String id, Material material) {
        Crate c = new Crate(id.toLowerCase(), material);
        crates.put(c.id, c);
        save();
        return c;
    }

    public void remove(String id) {
        crates.remove(id.toLowerCase());
        save();
    }

    public ItemStack createItem(Crate c, int amount) {
        ItemStack item = new ItemStack(c.material, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Msg.c(c.name));
        if (!c.lore.isEmpty()) {
            List<Component> lore = Msg.lore(c.lore);
            meta.lore(lore);
        }
        if (c.model > 0) {
            CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
            cmd.setFloats(List.of((float) c.model));
            meta.setCustomModelDataComponent(cmd);
        }
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, c.id);
        item.setItemMeta(meta);
        return item;
    }

    public String idOf(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }
        return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
}
