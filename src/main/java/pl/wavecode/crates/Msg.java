package pl.wavecode.crates;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public final class Msg {

    public static final String PREFIX = "&bᴡᴀᴠᴇ&fᴄᴏᴅᴇ &7» ";

    private static final String SMALL = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ";
    private static final String PL_FROM = "ąćęłńóśźżĄĆĘŁŃÓŚŹŻ";
    private static final String PL_TO = "acelnoszzACELNOSZZ";
    private static final String CODES = "0123456789abcdefklmnorABCDEFKLMNOR";
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private Msg() {
    }

    public static String t(String in) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < in.length(); i++) {
            char ch = in.charAt(i);
            if (ch == '&' && i + 1 < in.length() && CODES.indexOf(in.charAt(i + 1)) >= 0) {
                sb.append(ch).append(in.charAt(i + 1));
                i++;
                continue;
            }
            int pl = PL_FROM.indexOf(ch);
            if (pl >= 0) {
                ch = PL_TO.charAt(pl);
            }
            ch = Character.toLowerCase(ch);
            if (ch >= 'a' && ch <= 'z') {
                sb.append(SMALL.charAt(ch - 'a'));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static Component c(String text) {
        return LEGACY.deserialize(t(text)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
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
}
