package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.OneCore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import io.github.zoltus.onecore.player.teleporting.LocationUtils;
import io.github.zoltus.onecore.utils.SlowingScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TestListener implements Listener {

    @EventHandler
    public void onChatt(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();

        List<String> argsList = new ArrayList<>(Arrays.asList(msg.split(" ")));
        String cmd = argsList.get(0);
        argsList.remove(0);
        String[] args = argsList.toArray(new String[0]);
        if (cmd.startsWith("/")) {
            switch (cmd.toLowerCase()) {
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
                    p.sendMessage("created " + i +  " users");
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
                    long start = System.currentTimeMillis();
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

                case "/tas1" -> {
                    Location loc = p.getLocation();
                    LocationUtils.teleportSafeAsync(p, new Location(loc.getWorld(), 0, 70, 0));
                    p.sendMessage("asd");
                }
                case "/rtp2" -> OneCore.getPlugin().getRtpHandler().queue(p.getUniqueId());
                case "/rtptimer" -> {
                    int i = Integer.parseInt(args[0]);
                    OneCore.getPlugin().getRtpHandler().changeQueueTimer(i);
                }
                case "/sht" -> {
                    int i = Integer.parseInt(args[0]);
                    scheduler = new SlowingScheduler(OneCore.getPlugin(), i, false, () -> p.sendMessage("Task " + i));
                }

                case "/shtset" -> {
                    int i = Integer.parseInt(args[0]);
                    scheduler.reSchedule(i);
                }
            }
        }
    }

    public static SlowingScheduler scheduler;
}
