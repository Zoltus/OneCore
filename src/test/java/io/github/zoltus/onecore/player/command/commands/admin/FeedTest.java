package io.github.zoltus.onecore.player.command.commands.admin;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.jorel.commandapi.test.TestBase;
import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.FEED_YOU_HAVE_BEEN_HEALED;
import static org.junit.jupiter.api.Assertions.*;

public class FeedTest extends TestBase {

    PlayerMock player1;
    PlayerMock player2;

    @BeforeEach
    public void setUp() { //do each player stay?
        super.setUp(OneCore.class);
        player1 = server.addPlayer("player1");
        player2 = server.addPlayer("player2");
        plugin.getLogger().info("§aPlayers:" + server.getOnlinePlayers().size());
    }

    @AfterEach
    public void tearDown() {
        Bukkit.getScheduler().cancelTasks(plugin);
        MockBukkit.unmock();
    }

    @Test
    void feed_WithPerm() {
        zeroLevels();
        player1.addAttachment(plugin, Commands.FEED_LABEL.asPermission(), true);
        player1.performCommand("feed");
        assertMSGReceived(Lang.FEED_YOU_HAVE_BEEN_HEALED.getString(), player1.nextMessage());
    }

    @Test
    void feed_WithoutPerm() {
        zeroLevels();
        player1.addAttachment(plugin, Commands.FEED_LABEL.asPermission(), false);
        player1.performCommand("feed player2");
        assertTrue(hasBeenHealed());
        assertNotEquals(Lang.FEED_YOU_HAVE_BEEN_HEALED.getString(), player1.nextMessage());
    }

    private boolean hasBeenHealed() {
        if (player1.isOnline()) {
            return player1.getFoodLevel() == 20
                    && player1.getSaturation() == 10.0F;
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(player1);
            return nbtPlayer.getFoodLevel() == 20
                    && nbtPlayer.getSaturationLevel() == 10.0F;
        }
    }

    public static void assertMSGReceived(String expected, String actual) {
        assertEquals(ChatColor.translateAlternateColorCodes('&', expected), actual);
    }

    //Unfeeds player
    private void zeroLevels() {
        if (player1.getPlayer() != null) {
            player1.setFoodLevel(3);
            player1.setSaturation(3.0F);
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(player1);
            nbtPlayer.setFoodLevel(3);
            nbtPlayer.setSaturationLevel(3.0F);
            nbtPlayer.save();
        }
    }

    // Feeds player
    private void feed() {
        if (player1.getPlayer() != null) {
            player1.setFoodLevel(20);
            player1.setSaturation(10.0F);
            FEED_YOU_HAVE_BEEN_HEALED.send(player1);
        } else {
            NBTPlayer nbtPlayer = new NBTPlayer(player1);
            nbtPlayer.setFoodLevel(20);
            nbtPlayer.setSaturationLevel(10.0F);
            nbtPlayer.save();
        }
    }
}