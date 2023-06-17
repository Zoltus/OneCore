package io.github.zoltus.onecore.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.zoltus.onecore.data.configuration.yamls.Config.CHAT_REMOVE_DUPLICATE_SPACES;
import static io.github.zoltus.onecore.data.configuration.yamls.Config.CHAT_TRIM;

public class ChatListener implements Listener {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer lcs = LegacyComponentSerializer.legacySection();

    //Chat listening event
    @EventHandler
    public void asyncChatEvent(AsyncPlayerChatEvent e) {
        handleChatFormat(e);
        handleMentions(e);
    }

    //Todo split the class
    private final List<String> wgCommands = List.of(
            "define", "create", "remove", "delete", "redefine", "update", "move",
            "claim", "addmember", "addowner", "removemember", "select", "info", "flags", "list",
            "flag", "setpriority", "setparent", "teleport", "load", "reload", "save", "write");

    private final String[] wgRegionCommands = {"flag", "flags", "select",
            "sel", "s", "remove", "rem", "delete",
            "del", "move", "update", "claim", "addmember", "addmem", "am", "addowner", "ao", "removemember",
            "remmember", "remmem", "rm", "removeowner", "ro", "info", "i",
            "setpriority", "priority", "pri", "setparent", "parent", "par", "teleport"};

    @EventHandler
    public void tabcomplete(TabCompleteEvent event) {
        String buffer = event.getBuffer();
        if (!(event.getSender() instanceof Player player)) {
            return;
        }
        //If worldguard enabled add tabcomplete for regions
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")
                && buffer.startsWith("/rg")) {
            RegionManager rgManager = WorldGuard.getInstance().getPlatform()
                    .getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
            if (rgManager == null || !StringUtils.startsWithAny(buffer, "/rg", "/region")) {
                return;
            }
            List<String> completions = event.getCompletions();
            //-1 makes sure it counts the last space as new arg
            String[] args = buffer.split("\\s", -1);
            //rg <complete>
            if (args.length == 2) {
                completions = wgCommands;
                ///rg <action> <complete>
            } else if (args.length == 3 && StringUtils.startsWithAny(args[1], wgRegionCommands)) {
                completions = List.copyOf(rgManager.getRegions().keySet());
                //Rg flag <region> <complete>
            } else if (args.length == 4 && StringUtils.startsWithAny(args[1], "flag", "f")) {
                List<Flag<?>> allFlags = WorldGuard.getInstance().getFlagRegistry().getAll();
                completions = allFlags.stream().map(Flag::getName).toList();
                //Rg flag <region> <flag> <complete>
            } else if (args.length == 5 && StringUtils.startsWithAny(args[1], "flag", "f")) {
                completions = List.of("allow", "deny", "none");
            }

            //Gets the current argument string for filtering the result
            String currentArg = args[args.length - 1];
            event.setCompletions(filter(currentArg, completions));
        }
    }

    private List<String> filter(String input, List<String> suggestions) {
        List<String> list = new ArrayList<>();
        for (String word : suggestions) {
            if (StringUtils.startsWithIgnoreCase(word, input)) {
                list.add(word);
            }
        }
        return list;
    }

    //todo @everyone bugs a bit with normal mentions if combines "hi@everyone a dd@Zoltus abb"
    private void handleMentions(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission(Config.MENTION_PERMISSION.asPermission())) {
            return;
        }
        String message = e.getMessage();
        //Handle @<player>
        Matcher matcher = Pattern.compile("@(\\w+)|@everyone").matcher(message);
        while (matcher.find()) {
            int start = matcher.start();
            String beforeColor = ChatColor.getLastColors(message.substring(0, start));
            String continueColor = StringUtils.defaultIfEmpty(beforeColor, "§f");
            //Handle @everyone
            if (matcher.group().equals("@everyone")) {
                if (p.hasPermission(Config.MENTION_EVERYONE_PERMISSION.asPermission())) {
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        message = message.replace(matcher.group(), Config.MENTION_COLOR.getString()
                                + "@Everyone" + continueColor);
                        target.playSound(target, Sound.valueOf(Config.MENTION_SOUND.get()), 1, 1);
                    }
                }
            } else {
                Player target = Bukkit.getPlayer(matcher.group(1));
                if (target != null /*&& !player.equals(sender)*/) {
                    message = message.replace(matcher.group(), Config.MENTION_COLOR.getString()
                            + target.getDisplayName() + continueColor);
                    target.playSound(target, Sound.valueOf(Config.MENTION_SOUND.get()), 1, 1);
                }
            }
        }
        String colorFormatted = translareColors(message);
        e.setMessage(colorFormatted);
    }

    //todo better errorcatch
    // String.format(format, this.player, this.message);
    private void handleChatFormat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        //Enables chat colors
        if (Config.CHAT_COLORS_ENABLED.getBoolean()
                && player.hasPermission(Config.CHAT_COLOR_PERMISSION.asPermission())) {
            e.setMessage(translareColors(e.getMessage()));
        }
        //Formats chat
        if (Config.CHAT_FORMATTER_ENABLED.getBoolean()) {
            String format = Config.CHAT_FORMAT.getString();
            format = format.replace("{0}", "%s");
            format = format.replace("{1}", "%s");
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = PlaceholderAPI.setPlaceholders(player, format);
            }
            if (CHAT_REMOVE_DUPLICATE_SPACES.getBoolean()) {
                format = format.replace("  ", " ");
            }
            if (CHAT_TRIM.getBoolean()) {
                format = format.trim();
            }
            //replaces %s with the player name and the message
            if (player.hasPermission(Config.CHAT_COLOR_PERMISSION.asPermission())) {
                format = translareColors(format);
            }
            try {
                e.setFormat(format);
            } catch (Exception ex) {
                System.out.println("Error while formatting chat message! "
                        + "This might be caused by invalid placeholders in the chat format!"
                        + "Have you installed PlaceholderAPI and its expansion? /papi ecloud download <expansion>");
            }
        }
    }

    public static String translareColors(String str) {
        str = lcs.serialize(mm.deserialize(str.replace("§", "&")));
        str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }


}



