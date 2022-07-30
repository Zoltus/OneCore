package sh.zoltus.onecore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class FakeBreak {

    /**
     * Checks if player could break the block on the location
     *
     * @param p   Player
     * @param loc Location
     * @return boolean
     */
    public static boolean canBreak(Player p, Location loc) {
        BlockBreakEvent breakEvent = new BlockBreakEvent(loc.getBlock(), p);
        Bukkit.getServer().getPluginManager().callEvent(breakEvent);
        if (!breakEvent.isCancelled()) {
            breakEvent.setCancelled(true);
            return true;
        }
        return false;
    }
}
