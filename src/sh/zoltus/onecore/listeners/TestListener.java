package sh.zoltus.onecore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.database.Database;
import sh.zoltus.onecore.player.command.User;
import sh.zoltus.onecore.player.nbt.NBTPlayer;
import sh.zoltus.onecore.player.teleporting.LocationUtils;
import sh.zoltus.onecore.utils.SlowingScheduler;

import java.security.SecureRandom;
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
        User user = User.of(p);

        if (cmd.startsWith("/")) {
            switch (cmd.toLowerCase()) {
                case "/testa" -> {
                    p.sendMessage("&a&lpatea");
                    p.sendMessage("│§8¸ᐧᑊ            §f│");//l &l &l &l &l &l &l
                }
                case "/loc" -> {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    if (target.hasPlayedBefore()) {
                        NBTPlayer nbtP = new NBTPlayer(target);
                        p.sendMessage("§aInv: " + nbtP.getInventoryItems());
                        p.sendMessage("§bh: " + nbtP.getHealth());
                        nbtP.setHealth(5);
                        p.sendMessage("§ch2: " + nbtP.getHealth());
                        nbtP.save();
                    }
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

                case "/sserver" -> {
                    e.setCancelled(true);
                    Database.database().saveServerAsync();
                    p.sendMessage("serverstuffSaved");
                }
                case "/splayers" -> {
                    e.setCancelled(true);
                    Database.database().saveUsersAsync();
                    p.sendMessage("saved all users");
                }

                case "/smoney" -> {
                    e.setCancelled(true);
                    int amount = Integer.parseInt(args[0]);
                    user.setBalance(amount);
                    p.sendMessage("set money to " + amount);
                    p.sendMessage("current amount = " + user.getBalance());
                }

                case "/smoneya" -> {
                    e.setCancelled(true);
                    p.sendMessage("current amount = " + user.getBalance());
                }

                case "/testloadusers" -> {
                    int amount = Integer.parseInt(args[0]);
                    while (amount != 0) {
                        //  OneUser.testLoad(p);
                        amount--;
                    }
                    p.sendMessage("finished testing users");
                }
                //  clickCommands.put("/" + this.hashCode() + key, consumer);
                case "/dbtest" -> Database.database().loadEconomyAsync();
                case "/test22" -> {
                    p.sendMessage("asdasd");
                    p.teleport(user.getHomes().get(user.getHomeArray()[0]).toLocation());
                }
                case "/dump" -> {
                    // user.getSettings().forEach((s, o) -> p.sendMessage("§7s: " + s + " o:" + o));
                    // user.getHomes().forEach((s, o) -> p.sendMessage("§as: " + s + " o:" + o));
                }
                case "/asynctest" -> {
                    long time = System.currentTimeMillis();
                    int i = Integer.parseInt(args[0]);
                    SecureRandom random = new SecureRandom();
                    while (i != 0) {
                        //OneEconomy.getBalances().put(UUID.randomUUID(), random.nextDouble(100000));
                        i--;
                    }//1063, 10959 10504
                    p.sendMessage("took:  " + (System.currentTimeMillis() - time));
                    // user.getSettings().forEach((s, o) -> p.sendMessage("§7s: " + s + " o:" + o));
                    // user.getHomes().forEach((s, o) -> p.sendMessage("§as: " + s + " o:" + o));
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
                case "/rtp2" -> {
                    OneCore.getPlugin().getRtpHandler().queue(p.getUniqueId());
                }
                case "/rtptimer" -> {
                    int i = Integer.parseInt(args[0]);
                    OneCore.getPlugin().getRtpHandler().changeQueueTimer(i);
                }
                case "/sht" -> {
                    int i = Integer.parseInt(args[0]);
                    scheduler = new SlowingScheduler(OneCore.getPlugin(), i,false, () -> {
                        p.sendMessage("Task " + i);
                    });
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
