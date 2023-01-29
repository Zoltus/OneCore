package io.github.zoltus.onecore.player.command.commands.admin;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.jorel.commandapi.ArgumentTree;
import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.zoltus.onecore.data.configuration.IConfig.PLAYER_PH;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.FEED_YOU_FED_TARGET;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.FEED_YOU_HAVE_BEEN_HEALED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FeedTest {

    OneCore plugin;
    PlayerMock player;
    ServerMock server;

    @BeforeEach
    void setUp() { //do each player stay?
        server = MockBukkit.mock();
        plugin = MockBukkit.load(OneCore.class);
        player = server.addPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        plugin.reloadConfig();
        plugin.getLogger().info("§aPlayers:" + server.getOnlinePlayers().size());
    }

    @AfterEach
    void tearDown() {
        Bukkit.getScheduler().cancelTasks(plugin);
        MockBukkit.unmock();
    }

    @Test
    void feed_WithPerm() {
        //feed <player>
        ArgumentTree arg0 = new OfflinePlayerArgument()
                .executes((sender, args) -> {
                    OfflinePlayer target = (OfflinePlayer) args[0];
                    feed(target);
                    if (target.getPlayer() != sender) {
                        FEED_YOU_FED_TARGET.send(sender, PLAYER_PH, target.getName());
                    }
                });
        //feed
        new Command(Commands.FEED_LABEL)
                .withPermission(Commands.FEED_PERMISSION)
                .withAliases(Commands.FEED_ALIASES)
                .executesPlayer((player, args) -> {
                    feed(player);
                }).then(arg0).override();

        player.setSaturation(0);
        player.addAttachment(plugin, Commands.FEED_LABEL.asPermission(), true);
        player.performCommand("feed");
        assertEquals(20, player.getSaturation());
        assertMSGReceived(Lang.FEED_YOU_HAVE_BEEN_HEALED.getString() + "a", player.nextMessage());
    }

    @Test
    void feed_WithoutPerm() {
        player.setSaturation(0);
        player.addAttachment(plugin, Commands.FEED_LABEL.asPermission(), false);
        player.performCommand("feed");
        assertEquals(0, player.getSaturation());
        assertNotEquals(Lang.FEED_YOU_HAVE_BEEN_HEALED.getString(), player.nextMessage());
    }

    public static void assertMSGReceived(String expected, String actual) {
        assertEquals(ChatColor.translateAlternateColorCodes('&', expected), actual);
    }


    // Feeds player
    private void feed(OfflinePlayer offP) {
        if (offP.getPlayer() != null) {
            Player p = offP.getPlayer();
            p.setFoodLevel(20);
            p.setSaturation(20.0F);
            p.setExhaustion(0.0f);
            FEED_YOU_HAVE_BEEN_HEALED.send(p);
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(offP);
            nbtPlayer.setfoodLevel(20);
            nbtPlayer.setfoodSaturationLevel(20.0F);
            nbtPlayer.setFoodExhaustionLevel(0.0F);
            nbtPlayer.save();
        }
    }
}