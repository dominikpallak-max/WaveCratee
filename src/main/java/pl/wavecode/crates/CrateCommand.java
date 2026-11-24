package pl.wavecode.crates;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CrateCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION = "wavecrates.admin";
    private static final List<String> SUBS = List.of("stworz", "edytuj", "daj", "usun", "lista", "ustaw", "przeladuj", "pomoc");
    private static final List<String> OPTIONS = List.of("nazwa", "lore", "blok", "animacja", "model", "glow", "wlaczona");

    private static final String[] HELP = {
            "&b/wskrzynka stworz <nazwa> &8- &7tworzy skrzynkę z bloku w ręce",
            "&b/wskrzynka edytuj <nazwa> &8- &7otwiera menu edycji",
            "&b/wskrzynka daj <gracz> <nazwa> [ilość] &8- &7daje skrzynkę graczowi",
            "&b/wskrzynka usun <nazwa> &8- &7usuwa skrzynkę",
            "&b/wskrzynka lista &8- &7lista wszystkich skrzynek",
            "&b/wskrzynka ustaw <nazwa> <opcja> <wartość> &8- &7szybka edycja komendą",
            "&b/wskrzynka przeladuj &8- &7wczytuje dane od nowa",
            "&7Opcje ustaw: &fnazwa, lore, blok, animacja, model, glow, wlaczona"
    };

    private final WaveCrates plugin;

    public CrateCommand(WaveCrates plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            Msg.send(sender, "&cNie masz uprawnień do tej komendy.");
            return true;
        }
        if (args.length == 0) {
            help(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "stworz" -> create(sender, args);
            case "edytuj" -> edit(sender, args);
            case "daj" -> give(sender, args);
            case "usun" -> delete(sender, args);
            case "lista" -> list(sender);
            case "ustaw" -> set(sender, args);
            case "przeladuj" -> {
                plugin.crates().load();
                Msg.send(sender, "&aWczytano skrzynek: &f" + plugin.crates().all().size() + "&a.");
            }
            default -> help(sender);
        }
        return true;
    }

    private void help(CommandSender sender) {
        Msg.send(sender, "&7Dostępne komendy:");
        for (String line : HELP) {
            sender.sendMessage(Msg.c("&8 » " + line));
        }
    }

    private void notFound(CommandSender sender, String id) {
        Msg.send(sender, "&cNie ma skrzynki o nazwie &f" + id + "&c.");
    }

    private void create(CommandSender sender, String[] a) {
        if (!(sender instanceof Player p)) {
            Msg.send(sender, "&cTa komenda jest tylko dla graczy.");
            return;
        }
        if (a.length < 2) {
            Msg.send(p, "&cUżycie: &f/wskrzynka stworz <nazwa>");
            return;
        }
        String id = a[1].toLowerCase();
        if (!id.matches("[a-z0-9_]{1,24}")) {
            Msg.send(p, "&cNazwa może mieć tylko litery a-z, cyfry i _ (maks. 24 znaki).");
            return;
        }
        if (plugin.crates().get(id) != null) {
            Msg.send(p, "&cSkrzynka o takiej nazwie już istnieje.");
            return;
        }
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType().isAir() || !hand.getType().isBlock()) {
            Msg.send(p, "&cMusisz trzymać blok w ręce.");
            return;
        }
        Crate c = plugin.crates().create(id, hand.getType());
        c.name = "&b" + Msg.t(a[1]);
        plugin.crates().save();
        Msg.send(p, "&aStworzono skrzynkę &2" + id + "&a! Edytuj ją: &f/wskrzynka edytuj " + id);
    }

    private void edit(CommandSender sender, String[] a) {
        if (!(sender instanceof Player p)) {
            Msg.send(sender, "&cTa komenda jest tylko dla graczy.");
            return;
        }
        if (a.length < 2) {
            Msg.send(p, "&cUżycie: &f/wskrzynka edytuj <nazwa>");
            return;
        }
        Crate c = plugin.crates().get(a[1]);
        if (c == null) {
            notFound(p, a[1]);
            return;
        }
        Gui.openEdit(plugin, p, c);
    }

    private void give(CommandSender sender, String[] a) {
        if (a.length < 3) {
            Msg.send(sender, "&cUżycie: &f/wskrzynka daj <gracz> <nazwa> [ilość]");
            return;
        }
        Player target = Bukkit.getPlayerExact(a[1]);
        if (target == null) {
            Msg.send(sender, "&cGracz &f" + a[1] + " &cjest offline.");
            return;
        }
        Crate c = plugin.crates().get(a[2]);
        if (c == null) {
            notFound(sender, a[2]);
            return;
        }
        int amount = 1;
        if (a.length > 3) {
            try {
                amount = Math.max(1, Math.min(2304, Integer.parseInt(a[3])));
            } catch (NumberFormatException ex) {
                Msg.send(sender, "&cIlość musi być liczbą.");
                return;
            }
        }
        ItemStack proto = plugin.crates().createItem(c, 1);
        int max = proto.getMaxStackSize();
        int left = amount;
        while (left > 0) {
            ItemStack stack = proto.clone();
            stack.setAmount(Math.min(max, left));
            left -= stack.getAmount();
            for (ItemStack over : target.getInventory().addItem(stack).values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), over);
            }
        }
        Msg.send(sender, "&aDałeś graczowi &f" + target.getName() + " &askrzynkę &f" + c.id + " &ax" + amount + "&a.");
        Msg.send(target, "&aOtrzymałeś skrzynkę &f" + c.name + " &ax" + amount + "&a.");
    }

    private void delete(CommandSender sender, String[] a) {
        if (a.length < 2) {
            Msg.send(sender, "&cUżycie: &f/wskrzynka usun <nazwa>");
            return;
        }
        if (plugin.crates().get(a[1]) == null) {
            notFound(sender, a[1]);
            return;
        }
        plugin.crates().remove(a[1]);
        Msg.send(sender, "&aUsunięto skrzynkę &f" + a[1].toLowerCase() + "&a.");
    }

    private void list(CommandSender sender) {
        if (plugin.crates().all().isEmpty()) {
            Msg.send(sender, "&7Nie ma jeszcze żadnych skrzynek.");
            return;
        }
        Msg.send(sender, "&7Skrzynki (&f" + plugin.crates().all().size() + "&7):");
        for (Crate c : plugin.crates().all()) {
            sender.sendMessage(Msg.c("&8 » &b" + c.id + " &8- " + (c.enabled ? "&aWłączona" : "&cWyłączona")
                    + " &8- &7animacja: &f" + c.anim.display + " &8- &7drop: &f" + c.drops.size()));
        }
    }

    private void set(CommandSender sender, String[] a) {
        if (a.length < 3) {
            Msg.send(sender, "&cUżycie: &f/wskrzynka ustaw <nazwa> <opcja> <wartość>");
            Msg.send(sender, "&7Opcje: &f" + String.join(", ", OPTIONS));
            return;
        }
        Crate c = plugin.crates().get(a[1]);
        if (c == null) {
            notFound(sender, a[1]);
            return;
        }
        String option = a[2].toLowerCase();
        String value = String.join(" ", Arrays.copyOfRange(a, 3, a.length));

        switch (option) {
            case "nazwa" -> {
                if (value.isBlank()) {
                    Msg.send(sender, "&cPodaj nową nazwę.");
                    return;
                }
                c.name = Msg.t(value);
            }
            case "lore" -> c.lore = Crate.parseLore(value);
            case "blok" -> {
                if (!(sender instanceof Player p)) {
                    Msg.send(sender, "&cTa opcja jest tylko dla graczy.");
                    return;
                }
                ItemStack hand = p.getInventory().getItemInMainHand();
                if (hand.getType().isAir() || !hand.getType().isBlock()) {
                    Msg.send(p, "&cMusisz trzymać blok w ręce.");
                    return;
                }
                c.material = hand.getType();
            }
            case "animacja" -> {
                Anim anim = Anim.find(value);
                if (anim == null) {
                    Msg.send(sender, "&cNie ma takiej animacji. Użyj TAB, aby zobaczyć listę.");
                    return;
                }
                c.anim = anim;
            }
            case "model" -> {
                try {
                    c.model = Math.max(0, Integer.parseInt(value.trim()));
                } catch (NumberFormatException ex) {
                    Msg.send(sender, "&cTo nie jest liczba.");
                    return;
                }
            }
            case "glow" -> c.glowing = bool(value);
            case "wlaczona" -> c.enabled = bool(value);
            default -> {
                Msg.send(sender, "&cNieznana opcja. Dostępne: &f" + String.join(", ", OPTIONS));
                return;
            }
        }
        plugin.crates().save();
        Msg.send(sender, "&aZapisano &f" + option + " &adla skrzynki &f" + c.id + "&a.");
    }

    private boolean bool(String value) {
        String v = value.trim().toLowerCase();
        return v.equals("true") || v.equals("tak") || v.equals("on") || v.equals("1");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] a) {
        if (!sender.hasPermission(PERMISSION)) {
            return List.of();
        }
        if (a.length == 1) {
            return filter(SUBS, a[0]);
        }
        String sub = a[0].toLowerCase();
        if (a.length == 2) {
            return switch (sub) {
                case "edytuj", "usun", "ustaw" -> filter(plugin.crates().ids(), a[1]);
                case "daj" -> filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), a[1]);
                case "stworz" -> List.of("<nazwa>");
                default -> List.of();
            };
        }
        if (a.length == 3) {
            return switch (sub) {
                case "daj" -> filter(plugin.crates().ids(), a[2]);
                case "ustaw" -> filter(OPTIONS, a[2]);
                default -> List.of();
            };
        }
        if (a.length == 4) {
            if (sub.equals("daj")) {
                return filter(List.of("1", "8", "16", "32", "64"), a[3]);
            }
            if (sub.equals("ustaw")) {
                String opt = a[2].toLowerCase();
                if (opt.equals("animacja")) {
                    List<String> ids = new ArrayList<>();
                    for (Anim anim : Anim.values()) {
                        ids.add(anim.id());
                    }
                    return filter(ids, a[3]);
                }
                if (opt.equals("glow") || opt.equals("wlaczona")) {
                    return filter(List.of("true", "false"), a[3]);
                }
                if (opt.equals("model")) {
                    return List.of("<liczba>");
                }
                if (opt.equals("nazwa")) {
                    return List.of("<nowa nazwa>");
                }
                if (opt.equals("lore")) {
                    return List.of("<linia1|linia2>");
                }
            }
        }
        return List.of();
    }

    private List<String> filter(List<String> options, String typed) {
        String lower = typed.toLowerCase();
        List<String> out = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(lower)) {
                out.add(option);
            }
        }
        return out;
    }
}
