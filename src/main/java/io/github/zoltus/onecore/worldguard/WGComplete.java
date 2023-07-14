package io.github.zoltus.onecore.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WGComplete implements Listener {

    //Todo split the class
    private final List<String> wgCommands = List.of(
            "define", "create", "remove", "delete", "redefine", "update", "move",
            "claim", "addmember", "addowner", "removemember", "select", "info", "flags", "list",
            "flag", "setpriority", "setparent", "teleport", "load", "reload", "save", "write");

    private final String[] wgRegionCommands = {"flag", "flags", "select", "redefine",
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
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard") && buffer.startsWith("/rg")) {
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
                Set<String> regions = rgManager.getRegions().keySet();
                //Convert allRegions to arraylist:
                ArrayList<String> allRegions = new ArrayList<>(regions);
                allRegions.add("__global__");
                completions = List.copyOf(allRegions);
                //Rg flag <region> <complete>
            } else if (args.length == 4 && StringUtils.startsWithAny(args[1], "flag", "f")) {
                List<Flag<?>> allFlags = WorldGuard.getInstance().getFlagRegistry().getAll();
                completions = allFlags.stream().map(Flag::getName).toList();
                //Rg flag <region> <flag> <complete>
            } else if (args.length == 5 && StringUtils.startsWithAny(args[1], "flag", "f")) {
                completions = List.of("allow", "deny", "none");
            }//todo separate complete values for each flag

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

}
