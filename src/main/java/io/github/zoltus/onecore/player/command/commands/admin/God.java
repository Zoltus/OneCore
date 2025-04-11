package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;
import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;

public class God implements ICommand, Listener {
    @Override
    public void init() {
        //god <pelaaja>
        Argument<?> arg0 = new OfflinePlayerArgument()
                .withPermission(Commands.GOD_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    boolean result;
                    OfflinePlayer offP = (OfflinePlayer) args.get(0);
                    User target = User.of(offP);
                    if (target != null) {
                        target.setGod(result = !target.isGod());
                        GOD_SELF.rb(MODE_PH, result).send(target);
                    } else {
                        NBTPlayer nbtPlayer = new NBTPlayer(offP);
                        nbtPlayer.setInvulnerable(result = !nbtPlayer.getInvulnerable());
                        nbtPlayer.save();
                    }
                    if (sender != offP) {
                        GOD_OTHER.rb(PLAYER_PH, sender.getName())
                                .rb(MODE_PH, result)
                                .send(sender);
                    }
                });
        //god
        new Command(Commands.GOD_LABEL)
                .withPermission(Commands.GOD_PERMISSION)
                .withAliases(Commands.GOD_ALIASES)
                .executesPlayer((p, args) -> {
                    p.setInvulnerable(!p.isInvulnerable());
                    GOD_SELF.rb(MODE_PH, p.isInvulnerable())
                            .send(p);
                }).then(arg0).override();


        //Refresh vanished players action bar every 2.5s same for godmode?
        //todo test pefomance? probs light anyways
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
                () -> Bukkit.getOnlinePlayers().forEach(player -> {
                    User target = User.of(player);
                    if (target.isGod()) {
                        String actionbar = GOD_ACTION_BAR.get();
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
                    }
                }), 0, 50);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p && User.of(p).isGod()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFoodLevelChange(final FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player p && User.of(p).isGod()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotionSplashEvent(final PotionSplashEvent e) {
        for (final LivingEntity entity : e.getAffectedEntities()) {
            if (entity instanceof Player p && User.of(p).isGod()) {
                e.setIntensity(entity, 0d);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityCombust(final EntityCombustEvent e) {
        if (e.getEntity() instanceof Player p && User.of(p).isGod()) {
            e.setCancelled(true);
        }
    }
}

