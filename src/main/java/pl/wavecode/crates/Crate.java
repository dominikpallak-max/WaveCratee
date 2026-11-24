package pl.wavecode.crates;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Crate {

    public static final int MAX_LORE = 7;

    public final String id;
    public String name;
    public List<String> lore = new ArrayList<>();
    public Material material;
    public Anim anim = Anim.SPIRALA;
    public boolean glowing = true;
    public boolean enabled = true;
    public int model = 0;
    public List<ItemStack> drops = new ArrayList<>();

    public Crate(String id, Material material) {
        this.id = id;
        this.name = "&b" + Msg.t(id);
        this.material = material;
    }

    public static List<String> parseLore(String raw) {
        List<String> out = new ArrayList<>();
        if (raw == null || raw.isBlank() || raw.equalsIgnoreCase("brak")) {
            return out;
        }
        for (String line : raw.split("\\|")) {
            if (out.size() >= MAX_LORE) {
                break;
            }
            out.add(Msg.t(line.trim()));
        }
        return out;
    }
}
