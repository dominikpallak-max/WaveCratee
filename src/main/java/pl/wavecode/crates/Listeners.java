package pl.wavecode.crates;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Listeners implements Listener {

    private record Pending(String id, String field) {
    }

    private final WaveCrates plugin;
    private final Random random = new Random();
    private final Map<UUID, Pending> pending = new ConcurrentHashMap<>();

    public Listeners(WaveCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent e) {
        String id = plugin.crates().idOf(e.getItemInHand());
        if (id == null) {
            return;
        }
        e.setCancelled(true);

        Player p = e.getPlayer();
        Crate c = plugin.crates().get(id);
        if (c == null) {
            Msg.send(p, "&cTa skrzynka już nie istnieje.");
            return;
        }
        if (!c.enabled) {
            p.showTitle(Title.title(
                    Msg.c("&cWyłączona"),
                    Msg.c("&7Ta skrzynka jest chwilowo &fwyłączona")));
            return;
        }
        if (c.drops.isEmpty()) {
            Msg.send(p, "&cTa skrzynka nie ma ustawionego dropu.");
            return;
        }

        if (p.getGameMode() != GameMode.CREATIVE) {
            EquipmentSlot hand = e.getHand();
            ItemStack current = p.getInventory().getItem(hand);
            if (current.getAmount() <= 1) {
                p.getInventory().setItem(hand, null);
            } else {
                current.setAmount(current.getAmount() - 1);
                p.getInventory().setItem(hand, current);
            }
        }

        ItemStack reward = c.drops.get(random.nextInt(c.drops.size())).clone();
        AnimationPlayer.start(plugin, c, e.getBlockPlaced().getLocation(), p, reward);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof Gui.Holder holder)) {
            return;
        }
        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }
        Crate c = plugin.crates().get(holder.id);
        boolean top = e.getClickedInventory() == e.getInventory();

        if (c == null) {
            e.setCancelled(true);
            p.closeInventory();
            return;
        }

        switch (holder.type) {
            case EDIT -> clickEdit(e, p, c, top);
            case LORE -> clickLore(e, p, c, top);
            case ANIMS -> clickAnims(e, p, c, top, holder);
            case DROPS -> clickDrops(e, p, c, top);
        }
    }

    private boolean guard(InventoryClickEvent e, boolean top) {
        if (top || e.isShiftClick() || e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            e.setCancelled(true);
        }
        return top;
    }

    private void later(Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    private void clickEdit(InventoryClickEvent e, Player p, Crate c, boolean top) {
        if (!guard(e, top)) {
            return;
        }
        switch (e.getRawSlot()) {
            case Gui.SLOT_BLOCK -> {
                ItemStack cursor = e.getCursor();
                if (cursor != null && !cursor.getType().isAir() && cursor.getType().isBlock()) {
                    c.material = cursor.getType();
                    plugin.crates().save();
                    Gui.fillEdit(plugin, e.getInventory(), c);
                    Msg.send(p, "&aZmieniono blok skrzynki na &f" + c.material.name().toLowerCase() + "&a.");
                } else {
                    Msg.send(p, "&cWeź blok na kursor i kliknij ten przycisk.");
                }
            }
            case Gui.SLOT_NAME -> prompt(p, c, "nazwa", "&7Wpisz na czacie nową nazwę skrzynki. Kolory dodasz znakiem &f&&7 i kodem koloru.");
            case Gui.SLOT_LORE -> {
                p.closeInventory();
                later(() -> Gui.openLore(p, c));
            }
            case Gui.SLOT_MODEL -> prompt(p, c, "model", "&7Wpisz na czacie liczbę custom model data.");
            case Gui.SLOT_DROPS -> {
                p.closeInventory();
                later(() -> Gui.openDrops(p, c));
            }
            case Gui.SLOT_ANIM -> {
                p.closeInventory();
                later(() -> Gui.openAnims(p, c, 0));
            }
            case Gui.SLOT_GLOW -> {
                c.glowing = !c.glowing;
                plugin.crates().save();
                Gui.fillEdit(plugin, e.getInventory(), c);
            }
            case Gui.SLOT_GIVE -> {
                for (ItemStack left : p.getInventory().addItem(plugin.crates().createItem(c, 1)).values()) {
                    p.getWorld().dropItemNaturally(p.getLocation(), left);
                }
                Msg.send(p, "&aOtrzymałeś skrzynkę &f" + c.name + "&a.");
            }
            case Gui.SLOT_CLOSE -> p.closeInventory();
            case Gui.SLOT_TOGGLE -> {
                c.enabled = !c.enabled;
                plugin.crates().save();
                Gui.fillEdit(plugin, e.getInventory(), c);
                Msg.send(p, c.enabled ? "&aSkrzynka została włączona." : "&cSkrzynka została wyłączona.");
            }
            default -> {
            }
        }
    }

    private void clickLore(InventoryClickEvent e, Player p, Crate c, boolean top) {
        if (!guard(e, top)) {
            return;
        }
        int slot = e.getRawSlot();
        if (slot == Gui.SLOT_CLOSE) {
            p.closeInventory();
            later(() -> Gui.openEdit(plugin, p, c));
            return;
        }
        if (slot == Gui.SLOT_ADD) {
            if (c.lore.size() >= Crate.MAX_LORE) {
                Msg.send(p, "&cMaksymalnie " + Crate.MAX_LORE + " linijek lore.");
            } else {
                prompt(p, c, "lore", "&7Wpisz na czacie nową linijkę lore.");
            }
            return;
        }
        int idx = slot - Gui.LORE_START;
        if (slot >= Gui.LORE_START && slot < Gui.LORE_START + Crate.MAX_LORE && idx < c.lore.size()) {
            c.lore.remove(idx);
            plugin.crates().save();
            Gui.fillLore(e.getInventory(), c);
            Msg.send(p, "&aUsunięto linijkę lore.");
        }
    }

    private void clickAnims(InventoryClickEvent e, Player p, Crate c, boolean top, Gui.Holder holder) {
        if (!guard(e, top)) {
            return;
        }
        int slot = e.getRawSlot();
        if (slot == Gui.SLOT_CLOSE) {
            p.closeInventory();
            later(() -> Gui.openEdit(plugin, p, c));
            return;
        }
        if (slot == Gui.SLOT_PREV && holder.page > 0) {
            holder.page--;
            Gui.fillAnims(e.getInventory(), c, holder.page);
            return;
        }
        if (slot == Gui.SLOT_NEXT && holder.page < Gui.pages() - 1) {
            holder.page++;
            Gui.fillAnims(e.getInventory(), c, holder.page);
            return;
        }
        int index = Gui.animIndex(slot, holder.page);
        Anim[] all = Anim.values();
        if (index >= 0 && index < all.length) {
            c.anim = all[index];
            plugin.crates().save();
            Gui.fillAnims(e.getInventory(), c, holder.page);
            Msg.send(p, "&aWybrano animację &f" + c.anim.display + "&a.");
        }
    }

    private void clickDrops(InventoryClickEvent e, Player p, Crate c, boolean top) {
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            e.setCancelled(true);
            return;
        }
        int raw = e.getRawSlot();
        if (top) {
            if (raw == Gui.SLOT_DROP_BACK) {
                e.setCancelled(true);
                p.closeInventory();
                later(() -> Gui.openEdit(plugin, p, c));
                return;
            }
            if (!Gui.isDropSlot(raw)) {
                e.setCancelled(true);
            }
            return;
        }
        if (e.isShiftClick()) {
            e.setCancelled(true);
            ItemStack stack = e.getCurrentItem();
            if (stack == null || stack.getType().isAir()) {
                return;
            }
            Inventory inv = e.getInventory();
            for (int i = 0; i < Gui.DROP_MAX; i++) {
                int slot = Gui.dropSlot(i);
                ItemStack at = inv.getItem(slot);
                if (at == null || at.getType().isAir()) {
                    inv.setItem(slot, stack.clone());
                    e.setCurrentItem(null);
                    return;
                }
            }
            Msg.send(p, "&cBrak miejsca w dropie.");
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getInventory().getHolder() instanceof Gui.Holder holder)) {
            return;
        }
        int size = e.getInventory().getSize();
        for (int slot : e.getRawSlots()) {
            if (slot < size) {
                boolean allowed = holder.type == Gui.Type.DROPS && Gui.isDropSlot(slot);
                if (!allowed) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof Gui.Holder holder) || holder.type != Gui.Type.DROPS) {
            return;
        }
        Crate c = plugin.crates().get(holder.id);
        if (c == null) {
            return;
        }
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < Gui.DROP_MAX; i++) {
            ItemStack stack = e.getInventory().getItem(Gui.dropSlot(i));
            if (stack != null && !stack.getType().isAir()) {
                list.add(stack.clone());
            }
        }
        c.drops = list;
        plugin.crates().save();
        Msg.send(e.getPlayer(), "&aZapisano drop skrzynki &f" + c.id + "&a. Przedmiotów: &f" + list.size() + "&a.");
    }

    private void prompt(Player p, Crate c, String field, String message) {
        pending.put(p.getUniqueId(), new Pending(c.id, field));
        p.closeInventory();
        Msg.send(p, message);
        Msg.send(p, "&7Wpisz &canuluj&7, aby anulować.");
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        Pending pd = pending.remove(e.getPlayer().getUniqueId());
        if (pd == null) {
            return;
        }
        e.setCancelled(true);
        String text = PlainTextComponentSerializer.plainText().serialize(e.message()).trim();
        Player p = e.getPlayer();
        later(() -> apply(p, pd, text));
    }

    private void apply(Player p, Pending pd, String text) {
        Crate c = plugin.crates().get(pd.id());
        if (c == null) {
            return;
        }
        boolean lore = pd.field().equals("lore");

        if (text.equalsIgnoreCase("anuluj")) {
            Msg.send(p, "&7Anulowano.");
        } else {
            switch (pd.field()) {
                case "nazwa" -> c.name = Msg.t(text);
                case "lore" -> {
                    if (c.lore.size() < Crate.MAX_LORE) {
                        c.lore.add(Msg.t(text));
                    }
                }
                case "model" -> {
                    try {
                        c.model = Math.max(0, Integer.parseInt(text));
                    } catch (NumberFormatException ex) {
                        Msg.send(p, "&cTo nie jest liczba.");
                        Gui.openEdit(plugin, p, c);
                        return;
                    }
                }
                default -> {
                }
            }
            plugin.crates().save();
            Msg.send(p, "&aZapisano zmianę.");
        }

        if (lore) {
            Gui.openLore(p, c);
        } else {
            Gui.openEdit(plugin, p, c);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        pending.remove(e.getPlayer().getUniqueId());
    }
}
