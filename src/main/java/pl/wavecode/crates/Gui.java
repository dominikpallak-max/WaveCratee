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

    public static final int SLOT_BLOCK = 10, SLOT_NAME = 11, SLOT_LORE = 12, SLOT_MODEL = 13;
    public static final int SLOT_DROPS = 14, SLOT_ANIM = 15, SLOT_GLOW = 16;
    public static final int SLOT_PREVIEW = 22;
    public static final int SLOT_GIVE = 38, SLOT_CLOSE = 40, SLOT_TOGGLE = 42;
    public static final int SLOT_PREV = 39, SLOT_NEXT = 41;
    public static final int SLOT_ADD = 42;
    public static final int SLOT_DROP_BACK = 49;
    public static final int LORE_START = 10;
    public static final int PAGE_SIZE = 21;
    public static final int DROP_MAX = 28;

    public enum Type { EDIT, LORE, ANIMS, DROPS }

    public static final class Holder implements InventoryHolder {
        public final Type type;
        public final String id;
        public int page;
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

    private static Holder create(Type type, String id, int size, String title) {
        Holder holder = new Holder(type, id);
        holder.inventory = Bukkit.createInventory(holder, size, Msg.c(title));
        return holder;
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

    public static int dropSlot(int i) {
        return 10 + (i / 7) * 9 + i % 7;
    }

    public static boolean isDropSlot(int slot) {
        int r = slot / 9;
        int col = slot % 9;
        return r >= 1 && r <= 4 && col >= 1 && col <= 7;
    }

    public static int animIndex(int slot, int page) {
        int r = slot / 9;
        int col = slot % 9;
        if (r < 1 || r > 3 || col < 1 || col > 7) {
            return -1;
        }
        return page * PAGE_SIZE + (r - 1) * 7 + (col - 1);
    }

    public static int pages() {
        return (Anim.values().length - 1) / PAGE_SIZE + 1;
    }

    public static void openEdit(WaveCrates plugin, Player player, Crate c) {
        Holder h = create(Type.EDIT, c.id, 45, "&bᴡᴀᴠᴇ&fᴄᴏᴅᴇ &8» &7" + c.id);
        fillEdit(plugin, h.getInventory(), c);
        player.openInventory(h.getInventory());
    }

    public static void fillEdit(WaveCrates plugin, Inventory inv, Crate c) {
        inv.clear();

        inv.setItem(SLOT_BLOCK, item(c.material, "&bZmień blok",
                "&7Obecny: &f" + c.material.name().toLowerCase(),
                "",
                "&eKliknij trzymając blok na kursorze"));

        inv.setItem(SLOT_NAME, item(Material.NAME_TAG, "&bNazwa",
                "&7Obecna: &r" + c.name,
                "",
                "&eKliknij, aby zmienić"));

        inv.setItem(SLOT_LORE, item(Material.WRITABLE_BOOK, "&bLore",
                "&7Linijek: &f" + c.lore.size() + "&7/&f" + Crate.MAX_LORE,
                "",
                "&eKliknij, aby edytować"));

        inv.setItem(SLOT_MODEL, item(Material.PAINTING, "&bCustom model data",
                "&7Obecne: &f" + c.model,
                "",
                "&eKliknij, aby zmienić"));

        inv.setItem(SLOT_DROPS, item(Material.ENDER_CHEST, "&dDrop",
                "&7Przedmiotów: &f" + c.drops.size(),
                "",
                "&eKliknij, aby edytować"));

        inv.setItem(SLOT_ANIM, item(c.anim.icon, "&bAnimacja",
                "&7Obecna: &f" + c.anim.display,
                "",
                "&eKliknij, aby wybrać"));

        inv.setItem(SLOT_GLOW, item(c.glowing ? Material.GLOWSTONE_DUST : Material.GUNPOWDER, "&bŚwiecenie przedmiotów",
                "&7Obecnie: " + (c.glowing ? "&aTAK" : "&cNIE"),
                "",
                "&eKliknij, aby przełączyć"));

        inv.setItem(SLOT_PREVIEW, plugin.crates().createItem(c, 1));

        inv.setItem(SLOT_GIVE, item(Material.CHEST, "&aOdbierz skrzynkę",
                "&7Dodaje skrzynkę do Twojego ekwipunku"));

        inv.setItem(SLOT_CLOSE, item(Material.BARRIER, "&cZamknij"));

        if (c.enabled) {
            inv.setItem(SLOT_TOGGLE, item(Material.LIME_DYE, "&aWłączona",
                    "&7Skrzynkę można otwierać",
                    "",
                    "&eKliknij, aby wyłączyć"));
        } else {
            inv.setItem(SLOT_TOGGLE, item(Material.RED_DYE, "&cWyłączona",
                    "&7Skrzynki nie można otwierać",
                    "",
                    "&eKliknij, aby włączyć"));
        }
    }

    public static void openLore(Player player, Crate c) {
        Holder h = create(Type.LORE, c.id, 45, "&bᴡᴀᴠᴇ&fᴄᴏᴅᴇ &8» &7lore");
        fillLore(h.getInventory(), c);
        player.openInventory(h.getInventory());
    }

    public static void fillLore(Inventory inv, Crate c) {
        inv.clear();
        for (int i = 0; i < c.lore.size(); i++) {
            String line = c.lore.get(i);
            inv.setItem(LORE_START + i, item(Material.PAPER, line.isBlank() ? "&8(pusta linijka)" : line,
                    "&7Linijka &f" + (i + 1) + "&7/&f" + Crate.MAX_LORE,
                    "",
                    "&cKliknij, aby usunąć"));
        }
        if (c.lore.size() < Crate.MAX_LORE) {
            inv.setItem(SLOT_ADD, item(Material.ARROW, "&aDodaj linijkę",
                    "&7Kliknij i wpisz tekst na czacie",
                    "&7Wolnych miejsc: &f" + (Crate.MAX_LORE - c.lore.size())));
        } else {
            inv.setItem(SLOT_ADD, item(Material.BARRIER, "&cLimit linijek",
                    "&7Maksymalnie &f" + Crate.MAX_LORE + " &7linijek"));
        }
        inv.setItem(SLOT_CLOSE, item(Material.ARROW, "&cWróć"));
    }

    public static void openAnims(Player player, Crate c, int page) {
        Holder h = create(Type.ANIMS, c.id, 45, "&bᴡᴀᴠᴇ&fᴄᴏᴅᴇ &8» &7animacje");
        h.page = page;
        fillAnims(h.getInventory(), c, page);
        player.openInventory(h.getInventory());
    }

    public static void fillAnims(Inventory inv, Crate c, int page) {
        inv.clear();
        Anim[] all = Anim.values();
        for (int k = 0; k < PAGE_SIZE; k++) {
            int idx = page * PAGE_SIZE + k;
            if (idx >= all.length) {
                break;
            }
            Anim a = all[idx];
            boolean selected = a == c.anim;
            ItemStack stack = item(a.icon, "&b" + a.display,
                    "&7" + a.desc,
                    "",
                    selected ? "&a▶ Wybrana" : "&eKliknij, aby wybrać");
            if (selected) {
                ItemMeta meta = stack.getItemMeta();
                meta.setEnchantmentGlintOverride(true);
                stack.setItemMeta(meta);
            }
            inv.setItem(10 + (k / 7) * 9 + k % 7, stack);
        }
        if (page > 0) {
            inv.setItem(SLOT_PREV, item(Material.ARROW, "&ePoprzednia strona", "&7Strona &f" + page + "&7/&f" + pages()));
        }
        if (page < pages() - 1) {
            inv.setItem(SLOT_NEXT, item(Material.ARROW, "&eNastępna strona", "&7Strona &f" + (page + 2) + "&7/&f" + pages()));
        }
        inv.setItem(SLOT_CLOSE, item(Material.BARRIER, "&cWróć", "&7Strona &f" + (page + 1) + "&7/&f" + pages()));
    }

    public static void openDrops(Player player, Crate c) {
        Holder h = create(Type.DROPS, c.id, 54, "&bᴡᴀᴠᴇ&fᴄᴏᴅᴇ &8» &7drop: " + c.id);
        Inventory inv = h.getInventory();
        for (int i = 0; i < Math.min(DROP_MAX, c.drops.size()); i++) {
            inv.setItem(dropSlot(i), c.drops.get(i).clone());
        }
        inv.setItem(SLOT_DROP_BACK, item(Material.LIME_DYE, "&aZapisz i wróć",
                "&7Wrzuć przedmioty, które mogą wypaść",
                "&7Maksymalnie &f" + DROP_MAX));
        player.openInventory(inv);
    }
}
