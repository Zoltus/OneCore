package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.teleporting.LocationUtils;
import io.github.zoltus.onecore.utils.SpeedChangeScheduler;
import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class TestListener implements Listener {

    public static BaseComponent[] replacePlaceholder(String line, String placeholder, String replaceWith) {
        // create the string builder to build the new string
        ComponentBuilder sb = new ComponentBuilder();
        //Keeps previous colors
        ComponentBuilder.FormatRetention retention = ComponentBuilder.FormatRetention.FORMATTING;
        // split the line into sections based on the placeholder
        String[] sections = line.split(placeholder);
        // loop through all sections
        for (int i = 0; i < sections.length; i++) {
            sb.append(new TextComponent(sections[i]), retention);
            // check if there is a placeholder to be replaced
            if (i != sections.length - 1) {
                // add the hover event for the placeholder
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(replaceWith));
                // create a new text component with the placeholder
                TextComponent placeholderText = new TextComponent(placeholder);
                // set the hover event for the placeholder
                placeholderText.setHoverEvent(hoverEvent);
                // append the placeholder component to the stringbuilder
                sb.append(placeholderText, retention);
            }
        }
        return sb.create();
    }

    public static long testMS = 0;
    public static SpeedChangeScheduler scheduler;

    private CompletableFuture<Location> getRandomLocAsync(Player p) {
        return CompletableFuture.supplyAsync(() -> {
            Block b;
            Location loc;
            Supplier<Integer> randomInt = () -> ThreadLocalRandom.current().nextInt(-1000, 1000 + 1);
            do {
                //Check if scandelayms has passed
                    b = p.getWorld().getHighestBlockAt(randomInt.get(), randomInt.get(), HeightMap.MOTION_BLOCKING);
                    loc = LocationUtils.getSafeLocation(p, b.getLocation());
            } while (loc == null);
            //If scan timeouts it returns null location, adds 1 because block y
            loc.add(0.5, 1.1, 0.5);
            return p.getLocation();
        });
    }

    private void getRandomLocAsync2(Player p) {
            Block b;
            Location loc;
            Supplier<Integer> randomInt = () -> ThreadLocalRandom.current().nextInt(-1000, 1000 + 1);
            do {
                //Check if scandelayms has passed
                b = p.getWorld().getHighestBlockAt(randomInt.get(), randomInt.get(), HeightMap.MOTION_BLOCKING);
                loc = LocationUtils.getSafeLocation(p, b.getLocation());
            } while (loc == null);
            //If scan timeouts it returns null location, adds 1 because block y
            loc.add(0.5, 1.1, 0.5);
    }

    @EventHandler
    public void aaa(InventoryClickEvent e) {
        System.out.println(e.getClick().name());

    }

    private static long ms = 0;
    private static int size = 0;
    @EventHandler
    public void onChatt(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();

        List<String> argsList = new ArrayList<>(Arrays.asList(msg.split(" ")));
        String cmd = argsList.get(0);
        argsList.remove(0);
        String[] args = argsList.toArray(new String[0]);
        long start = System.currentTimeMillis();
        if (cmd.startsWith("/")) {
            switch (cmd.toLowerCase()) {
                case "/ta1" -> {
                    BaseComponent[] comps = replacePlaceholder("hi imxxxxxma a ixxxxx   xxx", " i", "xx");
                    p.spigot().sendMessage(comps);
                }
                case "/ta2" -> {
                    int amount = Integer.parseInt(args[0]);
                    size = amount;
                    ms = start;
                    p.sendMessage("start");
                    while (amount > 0) {
                        getRandomLocAsync(p).thenAccept(location -> {
                            if (size == 1) {
                                Bukkit.broadcastMessage("end " + (System.currentTimeMillis() - ms));
                            }
                            size--;
                        });
                        amount--;
                    }
                }
                case "/ta3" -> {
                    int amount = Integer.parseInt(args[0]);
                    p.sendMessage("start");
                    while (amount > 0) {
                        getRandomLocAsync2(p);
                        p.teleport(p.getLocation());
                        amount--;
                    }
                    p.sendMessage("done took: " + (System.currentTimeMillis() - start));
                }
                case "/testbots" -> {
                    int i = 0;
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        p.sendMessage("cehcking " + onlinePlayer.getName());
                        User of = User.of(onlinePlayer);
                        if (of == null) {
                            p.sendMessage("null");
                            new User(onlinePlayer);
                            i++;
                        } else {
                            p.sendMessage("not null");
                        }
                    }
                    p.sendMessage("created " + i + " users");
                }
                case "/t22" -> {
                    NamespacedKey line1 = new NamespacedKey(OneCore.getPlugin(), "line1");
                    NamespacedKey line2 = new NamespacedKey(OneCore.getPlugin(), "line2");
                    NamespacedKey line3 = new NamespacedKey(OneCore.getPlugin(), "line3");
                    NamespacedKey line4 = new NamespacedKey(OneCore.getPlugin(), "line4");
                    PersistentDataContainer cont = p.getPersistentDataContainer();
                    p.sendMessage("line1: " + cont.get(line1, PersistentDataType.STRING));
                    p.sendMessage("line2: " + cont.get(line2, PersistentDataType.STRING));
                    p.sendMessage("line3: " + cont.get(line3, PersistentDataType.STRING));
                    p.sendMessage("line4: " + cont.get(line4, PersistentDataType.STRING));
                    p.sendMessage(cont.toString());
                }
                case "/repeatcmd" -> {
                    e.setCancelled(true);
                    int amount = Integer.parseInt(args[0]);
                    argsList.remove(0);
                    String cmdCombinedString = String.join(" ", argsList);
                    p.sendMessage("§bAmount : " + amount + ": " + "cmd: " + cmdCombinedString);
                    while (amount != 0) {
                        p.performCommand(cmdCombinedString);
                        amount--;
                    }
                    p.sendMessage("Took " + (System.currentTimeMillis() - start));
                }
                case "/splayers" -> {
                    e.setCancelled(true);
                    OneCore.getPlugin().getDatabase().saveUsersAsync();
                    p.sendMessage("saved all users");
                }
                case "/testloadusers" -> {
                    int amount = Integer.parseInt(args[0]);
                    while (amount != 0) {
                        //  OneUser.testLoad(p);
                        amount--;
                    }
                    p.sendMessage("finished testing users");
                }
                case "/jointest" -> //When used remove OnePlayer.of from hashmap if statement
                        Bukkit.getScheduler().runTaskAsynchronously(OneCore.getPlugin(), () -> {
                            long time = System.currentTimeMillis();
                            int i = Integer.parseInt(args[0]);
                            AsyncPlayerPreLoginEvent asyncPrejoin = new AsyncPlayerPreLoginEvent("Zoltus", Objects.requireNonNull(p.getAddress()).getAddress(), p.getUniqueId());
                            Bukkit.broadcastMessage("§8started");
                            while (i != 0) {
                                Bukkit.getServer().getPluginManager().callEvent(asyncPrejoin);
                                i--;
                            }//1063, 10959 10504
                            p.sendMessage("joins took " + (System.currentTimeMillis() - time));
                        });
                case "/tes" -> {
                    p.sendMessage("test");
                    testMS = System.currentTimeMillis();
                    PaperLib.teleportAsync(p, p.getLocation().add(3, 2, 0));
                }
                case "/sht" -> {
                    int i = Integer.parseInt(args[0]);
                    scheduler = new SpeedChangeScheduler(OneCore.getPlugin(), i, false, () -> p.sendMessage("Task " + i));
                }
                case "/shtset" -> {
                    int i = Integer.parseInt(args[0]);
                    scheduler.reSchedule(i);
                }
            }
        }
    }
}
















