package pl.wavecode.crates;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public final class Msg {

    public static final String PREFIX = "&3ᴡᴀᴠᴇ&fᴄᴏᴅᴇ &7» ";

    private static final String SMALL = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ";
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private Msg() {
    }

    public static Component c(String text) {
        return LEGACY.deserialize(text).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static void send(CommandSender to, String text) {
        to.sendMessage(c(PREFIX + text));
    }

    public static List<Component> lore(String... lines) {
        List<Component> out = new ArrayList<>();
        for (String line : lines) {
            out.add(c(line));
        }
        return out;
    }

    public static List<Component> lore(List<String> lines) {
        List<Component> out = new ArrayList<>();
        for (String line : lines) {
            out.add(c(line));
        }
        return out;
    }

    public static String sc(String in) {
        StringBuilder sb = new StringBuilder();
        for (char ch : in.toLowerCase().toCharArray()) {
            if (ch >= 'a' && ch <= 'z') {
                sb.append(SMALL.charAt(ch - 'a'));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
