package pl.wavecode.crates;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class Gui {

    public static final int SLOT_BLOCK = 19, SLOT_NAME = 21, SLOT_LORE = 23, SLOT_MODEL = 25;
    public static final int SLOT_DROPS = 29, SLOT_ANIM = 31, SLOT_GLOW = 33;
    public static final int SLOT_GIVE = 48, SLOT_CLOSE = 49, SLOT_TOGGLE = 50;
    public static final int ANIM_START = 9;
    public static final int DROP_SLOTS = 45;

    public enum Type { EDIT, ANIMS, DROPS }

    public static final class Holder implements InventoryHolder {
        public final Type type;
        public final String id;
        private Inventory inventory;

        private Holder(Type type, String id) {
            this.type = type;
            this.id = id;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    private Gui() {
    }

    private static Inventory create(Type type, String id, String title) {
        Holder holder = new Holder(type, id);
        holder.inventory = Bukkit.createInventory(holder, 54, Msg.c(title));
        return holder.inventory;
    }

    private static ItemStack item(Material material, String name, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Msg.c(name));
        if (lore.length > 0) {
            meta.lore(Msg.lore(lore));
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        stack.setItemMeta(meta);
        return stack;
    }

    private static void fill(Inventory inv, int from, int to) {
        ItemStack pane = item(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = from; i < to; i++) {
            inv.setItem(i, pane.clone());
        }
    }

    public static void openEdit(WaveCrates plugin, Player player, Crate c) {
        Inventory inv = create(Type.EDIT, c.id, "&3ᴡᴀᴠᴇ&fᴄᴏᴅᴇ &8» &7" + c.id);
        fillEdit(plugin, inv, c);
        player.openInventory(inv);
    }

    public static void fillEdit(WaveCrates plugin, Inventory inv, Crate c) {
        fill(inv, 0, 54);
        inv.setItem(4, plugin.crates().createItem(c, 1));

        inv.setItem(SLOT_BLOCK, item(c.material, "&b" + Msg.sc("zmień blok"),
                "&7Obecny: &f" + c.material.name().toLowerCase(),
                "",
                "&eKliknij trzymając blok na kursorze"));

        inv.setItem(SLOT_NAME, item(Material.NAME_TAG, "&b" + Msg.sc("nazwa"),
                "&7Obecna: &r" + c.name,
                "",
                "&eKliknij, aby zmienić"));

        List<String> lore = new ArrayList<>();
        lore.add("&7Obecne:");
        if (c.lore.isEmpty()) {
            lore.add("&8brak");
        } else {
            for (String line : c.lore) {
                lore.add("&8 - &r" + line);
            }
        }
        lore.add("");
        lore.add("&eKliknij, aby zmienić");
        lore.add("&7Linie oddzielaj znakiem &f|");
        lore.add("&7Wpisz &fbrak&7, aby wyczyścić");
        inv.setItem(SLOT_LORE, item(Material.WRITABLE_BOOK, "&b" + Msg.sc("lore"), lore.toArray(new String[0])));

        inv.setItem(SLOT_MODEL, item(Material.PAINTING, "&b" + Msg.sc("custom model data"),
                "&7Obecne: &f" + c.model,
                "",
                "&eKliknij, aby zmienić"));

        inv.setItem(SLOT_DROPS, item(Material.ENDER_CHEST, "&d" + Msg.sc("drop"),
                "&7Przedmiotów: &f" + c.drops.size(),
                "",
                "&eKliknij, aby edytować"));

        inv.setItem(SLOT_ANIM, item(c.anim.icon, "&b" + Msg.sc("animacja"),
                "&7Obecna: &f" + c.anim.display,
                "",
                "&eKliknij, aby wybrać"));

        inv.setItem(SLOT_GLOW, item(c.glowing ? Material.GLOWSTONE_DUST : Material.GUNPOWDER, "&b" + Msg.sc("świecenie przedmiotów"),
                "&7Obecnie: " + (c.glowing ? "&aTAK" : "&cNIE"),
                "",
                "&eKliknij, aby przełączyć"));

        inv.setItem(SLOT_GIVE, item(Material.CHEST, "&a" + Msg.sc("odbierz skrzynkę"),
                "&7Dodaje skrzynkę do Twojego ekwipunku"));

        inv.setItem(SLOT_CLOSE, item(Material.BARRIER, "&c" + Msg.sc("zamknij")));

        if (c.enabled) {
            inv.setItem(SLOT_TOGGLE, item(Material.LIME_DYE, "&a" + Msg.sc("włączona"),
                    "&7Skrzynkę można otwierać",
                    "",
                    "&eKliknij, aby wyłączyć"));
        } else {
            inv.setItem(SLOT_TOGGLE, item(Material.RED_DYE, "&c" + Msg.sc("wyłączona"),
                    "&7Skrzynki nie można otwierać",
                    "",
                    "&eKliknij, aby włączyć"));
        }
    }

    public static void openAnims(Player player, Crate c) {
        Inventory inv = create(Type.ANIMS, c.id, "&3ᴡᴀᴠᴇ&fᴄᴏᴅᴇ &8» &7animacje");
        fillAnims(inv, c);
        player.openInventory(inv);
    }

    public static void fillAnims(Inventory inv, Crate c) {
        fill(inv, 0, 54);
        Anim[] all = Anim.values();
        for (int i = 0; i < all.length; i++) {
            Anim a = all[i];
            boolean selected = a == c.anim;
            ItemStack stack = item(a.icon, "&b" + Msg.sc(a.display),
                    "&7" + a.desc,
                    "",
                    selected ? "&a▶ " + Msg.sc("wybrana") : "&eKliknij, aby wybrać");
            if (selected) {
                ItemMeta meta = stack.getItemMeta();
                meta.setEnchantmentGlintOverride(true);
                stack.setItemMeta(meta);
            }
            inv.setItem(ANIM_START + i, stack);
        }
        inv.setItem(SLOT_CLOSE, item(Material.ARROW, "&c" + Msg.sc("wróć")));
    }

    public static void openDrops(Player player, Crate c) {
        Inventory inv = create(Type.DROPS, c.id, "&3ᴡᴀᴠᴇ&fᴄᴏᴅᴇ &8» &7drop: " + c.id);
        for (int i = 0; i < Math.min(DROP_SLOTS, c.drops.size()); i++) {
            inv.setItem(i, c.drops.get(i).clone());
        }
        fill(inv, DROP_SLOTS, 54);
        inv.setItem(SLOT_CLOSE, item(Material.LIME_DYE, "&a" + Msg.sc("zapisz i wróć"),
                "&7Wrzuć przedmioty, które mogą wypaść"));
        player.openInventory(inv);
    }
}
